/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.http.intercept;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorEventHandler;
import com.subgraph.vega.api.http.proxy.IProxyTransaction;
import com.subgraph.vega.api.http.proxy.IProxyTransactionEventHandler;

/**
 * Manages transactions held by the HTTP proxy interceptor on behalf of InterceptView.
 * 
 * The transaction manager employs a serial number to avoid race conditions caused by the delay signaling to the UI
 * thread that a change occurred in the transaction queue. The TransactionInfo class, which the UI uses to display the
 * transaction held by the manager after an event is received, records currentSerial when a transaction is pending. That
 * way, if the transaction queue changes before the next event reaches the UI, the UI can't accidentally forward the
 * wrong transaction. 
 */
public class TransactionManager {
	public enum TransactionStatus {
		STATUS_INACTIVE,
		STATUS_PENDING,
		STATUS_SENT,
	};
	
	private final Logger logger = Logger.getLogger("proxy");
	private InterceptView interceptView;
	private IHttpInterceptor interceptor;
	private IHttpInterceptorEventHandler interceptorEventHandler;
	private IProxyTransactionEventHandler transactionEventHandler;
	private IProxyTransaction currentTransaction;
	private IProxyTransaction currentRequestTransaction;
	private int currentSerial; // Serial number of current pending transaction
	private TransactionStatus requestStatus;
	private int requestTransactionSerial; // Serial number of last request change
	private TransactionStatus responseStatus;
	private int responseTransactionSerial; // Serial number of last response change
	
	TransactionManager(InterceptView interceptView, IHttpInterceptor interceptor) {
		this.interceptView = interceptView;
		this.interceptor = interceptor;
		interceptorEventHandler = new IHttpInterceptorEventHandler() {
			@Override
			public void notifyQueue(IProxyTransaction transaction, int idx) {
				if (transaction.hasResponse() == false) {
					handleTransactionRequest(transaction);
				} else {
					handleTransactionResponse(transaction);
				}
			}

			@Override
			public void notifyRemove(int idx) {
			}

			@Override
			public void notifyEmpty() {
			}
		};
		transactionEventHandler = new IProxyTransactionEventHandler() {
			@Override
			public void notifyForward() {
				handleTransactionForward();
			}

			@Override
			public void notifyComplete(boolean dropped) {
				handleTransactionComplete();
			}
		};
		currentSerial = 0;
		requestStatus = TransactionStatus.STATUS_INACTIVE;
		requestTransactionSerial = 0;
		responseStatus = TransactionStatus.STATUS_INACTIVE;
		responseTransactionSerial = 0;
		getNextTransaction();
	}
	
	/**
	 * Activate the transaction manager to handle interceptor events once elements of the intercept view are initialized.
	 */
	public void setManagerActive() {
		synchronized(this) {
			interceptor.addEventHandler(interceptorEventHandler);
			getNextTransaction();
		}
	}

	/**
	 * Set a transaction as the current transaction.
	 * @param transaction Transaction. 
	 */
	public void openTransaction(IProxyTransaction transaction) {
		synchronized(this) {
			if (currentTransaction != null) {
				if (currentTransaction == transaction) {
					return;
				}
				currentTransaction.setEventHandler(null);
			}
			setCurrentTransaction(transaction);
			interceptView.notifyUpdate();
		}
	}
	
	/**
	 * Close this transaction manager prior to the interceptor view being disposed. Unregisters the manager as an event
	 * handler in the interceptor and the current transaction, if one exists. 
	 */
	public void close() {
		synchronized(this) {
			if (interceptor != null) {
				interceptor.removeEventHandler(interceptorEventHandler);
				if (currentTransaction != null) {
					currentTransaction.setEventHandler(null);
					currentTransaction = currentRequestTransaction = null;
					currentSerial++;
					requestTransactionSerial++;
					responseTransactionSerial++;
					requestStatus = responseStatus = TransactionStatus.STATUS_INACTIVE;
				}
				interceptor = null;
			}
		}
	}

	/**
	 * Update transaction info with the transaction currently held by the manager.
	 * 
	 * @param transactionInfo Transaction info.
	 */
	public void updateTransactionInfo(TransactionInfo transactionInfo) {
		synchronized(this) {
			if (currentTransaction != null) {
				if (transactionInfo.getRequestTransactionSerial() != requestTransactionSerial) {
					try {
						transactionInfo.setFromRequest(currentTransaction.getRequest());
					} catch (URISyntaxException e) {
						logger.log(Level.WARNING, "Error processing request, dropped transaction", e);
						handleBadUpdate(transactionInfo);
						return;
					}
					if (requestStatus == TransactionStatus.STATUS_PENDING) {
						transactionInfo.setCurrentSerial(currentSerial);
					}
					transactionInfo.setRequestHasContent(true);
					transactionInfo.setRequestStatus(requestStatus);
					transactionInfo.setRequestTransactionSerial(requestTransactionSerial);
				} else {
					if (transactionInfo.getRequestStatus() != requestStatus) {
						if (requestStatus == TransactionStatus.STATUS_SENT) {
							transactionInfo.setCurrentSerial(currentSerial);
						}
						transactionInfo.setRequestStatus(requestStatus);
					}
				}

				if (transactionInfo.getResponseTransactionSerial() != responseTransactionSerial) {
					if (responseStatus != TransactionStatus.STATUS_INACTIVE) {
						try {
							transactionInfo.setFromResponse(currentTransaction.getResponse().getRawResponse());
						} catch (URISyntaxException e) {
							logger.log(Level.WARNING, "Error processing response, dropped transaction", e);
							handleBadUpdate(transactionInfo);
							return;
						}
						if (responseStatus == TransactionStatus.STATUS_PENDING) {
							transactionInfo.setCurrentSerial(currentSerial);
						}
						transactionInfo.setResponseHasContent(true);
					} else {
						transactionInfo.getResponseBuilder().clear();
						transactionInfo.setResponseHasContent(false);
					}
					transactionInfo.setResponseStatus(responseStatus);
					transactionInfo.setResponseTransactionSerial(responseTransactionSerial);
				} else {
					if (transactionInfo.getResponseStatus() != responseStatus) {
						transactionInfo.setResponseStatus(responseStatus);
					}
				}
			} else {
				if (transactionInfo.getRequestTransactionSerial() != requestTransactionSerial) {
					transactionInfo.getRequestBuilder().clear();
					transactionInfo.setRequestHasContent(false);
					transactionInfo.setRequestStatus(requestStatus);
					transactionInfo.setRequestTransactionSerial(requestTransactionSerial);
				}

				if (transactionInfo.getResponseTransactionSerial() != responseTransactionSerial) {
					transactionInfo.getResponseBuilder().clear();
					transactionInfo.setResponseHasContent(false);
					transactionInfo.setResponseStatus(responseStatus);
					transactionInfo.setResponseTransactionSerial(responseTransactionSerial);
				}
			}
		}
	}

	/**
	 * Drop a transaction following an error processing it into TransactionInfo and attempt to load the next queued
	 * transaction.
	 */
	private void handleBadUpdate(TransactionInfo transactionInfo) {
		currentTransaction.setEventHandler(null);
		currentTransaction.doDrop();
		getNextTransaction();
		updateTransactionInfo(transactionInfo);
	}
	
	private void handleTransactionRequest(final IProxyTransaction transaction) {
		synchronized(this) {
			if(currentTransaction == null) {
				currentTransaction = transaction;
				currentTransaction.setEventHandler(transactionEventHandler);
				requestTransactionSerial++;
				setRequestPending();
				interceptView.notifyUpdate();
			}
		}
	}

	private void handleTransactionResponse(final IProxyTransaction transaction) {
		synchronized(this) {
			if (currentTransaction == null || currentTransaction == transaction) {
				if (currentTransaction == null) {
					currentTransaction = transaction;
					currentTransaction.setEventHandler(transactionEventHandler);
					requestTransactionSerial++;
				}
				responseTransactionSerial++;
				setResponsePending();
				interceptView.notifyUpdate();
			}
		}
	}

	private void handleTransactionForward() {
		synchronized(this) {
			if (currentTransaction.hasResponse()) {
				currentTransaction.setEventHandler(null);
				getNextTransaction();
			} else {
				setRequestSent();
			}
			interceptView.notifyUpdate();
		}
	}

	private void handleTransactionComplete() {
		synchronized(this) {
			currentTransaction.setEventHandler(null);
			getNextTransaction();
			interceptView.notifyUpdate();
		}
	}
	
	/**
	 * Make the transaction at the head of the interceptor queue the current transaction. Must be invoked within a
	 * synchronized block.
	 */
	private void getNextTransaction() {
		setCurrentTransaction(interceptor.transactionQueueGet(0));
	}

	private void setCurrentTransaction(IProxyTransaction transaction) {
		currentTransaction = transaction;			
		requestTransactionSerial++;
		responseTransactionSerial++;
		if(currentTransaction != null) {
			currentTransaction.setEventHandler(transactionEventHandler);
			if (!currentTransaction.hasResponse()) {
				setRequestPending();
				setResponseInactive();
			} else {
				setResponsePending();
			}
		} else {
			setRequestInactive();
			setResponseInactive();
		}
	}

	private void setRequestPending() {
		currentRequestTransaction = currentTransaction;
		requestStatus = TransactionStatus.STATUS_PENDING;
		currentSerial++;
	}
	
	private void setRequestInactive() {
		currentRequestTransaction = currentTransaction;
		requestStatus = TransactionStatus.STATUS_INACTIVE;
		currentSerial++;
	}

	private void setRequestSent() {
		currentRequestTransaction = currentTransaction;
		requestStatus = TransactionStatus.STATUS_SENT;
		currentSerial++;
	}

	private void setResponseInactive() {
		responseStatus = TransactionStatus.STATUS_INACTIVE;
		currentSerial++;
	}

	private void setResponsePending() {
		responseStatus = TransactionStatus.STATUS_PENDING;
		currentSerial++;
		if(currentRequestTransaction != currentTransaction) {
			setRequestSent();
		}
	}

	public void forwardTransaction(TransactionInfo info) throws URISyntaxException, UnsupportedEncodingException {
		synchronized(this) {
			if (info.getCurrentSerial() == currentSerial) {
				if (requestStatus == TransactionManager.TransactionStatus.STATUS_PENDING) {
					HttpUriRequest request = info.getRequestBuilder().buildRequest(true);
					currentTransaction.setRequest(request);
					currentTransaction.doForward();
				} else if (responseStatus == TransactionManager.TransactionStatus.STATUS_PENDING) {
					HttpResponse response = info.getResponseBuilder().buildResponse();
					currentTransaction.getResponse().setRawResponse(response);
					currentTransaction.doForward();
				}
			}
		}
	}

	public void dropTransaction(TransactionInfo info) {
		synchronized(this) {
			if (info.getCurrentSerial() == currentSerial) {
				currentSerial++;
				currentTransaction.doDrop();
			}
		}
	}
	
}
