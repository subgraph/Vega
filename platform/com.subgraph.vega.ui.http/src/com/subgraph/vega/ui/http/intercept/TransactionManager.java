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
	private TransactionStatus requestStatus;
	private int requestTransactionSerial;
	private int requestSerial;
	private TransactionStatus responseStatus;
	private int responseSerial;
	private int responseTransactionSerial;
	
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
		requestStatus = TransactionStatus.STATUS_INACTIVE;
		requestSerial = 0;
		requestTransactionSerial = 0;
		responseStatus = TransactionStatus.STATUS_INACTIVE;
		responseSerial = 0;
		responseTransactionSerial = 0;
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
	 * Close this transaction manager prior to the interceptor view being closed. Unregisters the manager as an event
	 * handler in the interceptor and the current transaction, if one exists. 
	 */
	public void close() {
		synchronized(this) {
			if (interceptor != null) {
				interceptor.removeEventHandler(interceptorEventHandler);
				if (currentTransaction != null) {
					currentTransaction.setEventHandler(null);
					currentTransaction = currentRequestTransaction = null;
					requestTransactionSerial++;
					responseTransactionSerial++;
					requestSerial++;
					responseSerial++;
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
					transactionInfo.setRequestHasContent(true);
					transactionInfo.setRequestStatus(requestStatus);
					transactionInfo.setRequestSerial(requestSerial);
					transactionInfo.setRequestTransactionSerial(requestTransactionSerial);
				} else {
					if (transactionInfo.getRequestStatus() != requestStatus) {
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
						transactionInfo.setResponseHasContent(true);
					} else {
						transactionInfo.getResponseBuilder().clear();
						transactionInfo.setResponseHasContent(false);
					}
					transactionInfo.setResponseStatus(responseStatus);
					transactionInfo.setResponseSerial(responseSerial);
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
		requestSerial++;
	}
	
	private void setRequestInactive() {
		currentRequestTransaction = currentTransaction;
		requestStatus = TransactionStatus.STATUS_INACTIVE;
		requestSerial++;
	}

	private void setRequestSent() {
		currentRequestTransaction = currentTransaction;
		requestStatus = TransactionStatus.STATUS_SENT;
		requestSerial++;
	}

	private void setResponseInactive() {
		responseStatus = TransactionStatus.STATUS_INACTIVE;
		responseSerial++;
	}

	private void setResponsePending() {
		responseStatus = TransactionStatus.STATUS_PENDING;
		responseSerial++;
		if(currentRequestTransaction != currentTransaction) {
			setRequestSent();
		}
	}

	public void forwardRequest(TransactionInfo info) throws URISyntaxException, UnsupportedEncodingException {
		synchronized(this) {
			if (info.getRequestSerial() == requestSerial) {
				HttpUriRequest request = info.getRequestBuilder().buildRequest();
				currentTransaction.setRequest(request);
				requestSerial++;
				currentTransaction.doForward();
			}
		}
	}

	public void forwardResponse(TransactionInfo info) throws UnsupportedEncodingException {
		synchronized(this) {
			if (info.getResponseSerial() == responseSerial) {
				HttpResponse response = info.getResponseBuilder().buildResponse();
				currentTransaction.getResponse().setRawResponse(response);
				currentTransaction.doForward();
			}
		}
	}
	
	public void dropRequest(TransactionInfo info) {
		synchronized(this) {
			if (info.getRequestSerial() == requestSerial) {
				requestSerial++;
				currentTransaction.doDrop();
			}
		}
	}

	public void dropResponse(TransactionInfo info) {
		synchronized(this) {
			if (info.getResponseSerial() == responseSerial) {
				responseSerial++;
				currentTransaction.doDrop();
			}
		}
	}
	
}
