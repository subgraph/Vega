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
package com.subgraph.vega.ui.identity.identitiesview;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.api.model.identity.IAuthMethod;

public class AuthMethodComboViewer extends ComboViewer {
	public enum AuthMethodSelection {
		AUTH_METHOD_BASIC("basic http authentication", IAuthMethod.AuthMethodType.AUTH_METHOD_RFC2617),
		AUTH_METHOD_DIGEST("digest http authentication", IAuthMethod.AuthMethodType.AUTH_METHOD_RFC2617),
		AUTH_METHOD_NTLM("NTLM", IAuthMethod.AuthMethodType.AUTH_METHOD_NTLM),
		AUTH_METHOD_HTTP_MACRO("macro", IAuthMethod.AuthMethodType.AUTH_METHOD_HTTP_MACRO);
		
		private String name;
		private IAuthMethod.AuthMethodType authMethodType;

		private AuthMethodSelection(String name, IAuthMethod.AuthMethodType authMethodType) {
			this.name = name;
			this.authMethodType = authMethodType;
		}
		
		public String getName() {
			return name;
		}
		
		public IAuthMethod.AuthMethodType getAuthMethodType() {
			return authMethodType;
		}
	}

	public AuthMethodComboViewer(Composite parent) {
		super(parent, SWT.READ_ONLY);
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(createLabelProvider());
		final AuthMethodSelection[] values = AuthMethodSelection.values();
		setInput(values);
		setSelection(new StructuredSelection(values[0]));
		
	}

	private ILabelProvider createLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((AuthMethodSelection)element).getName();
			}
		};
	}

	public AuthMethodSelection getAuthMethodSelection() {
		return (AuthMethodSelection)((IStructuredSelection) getSelection()).getFirstElement();
	}
	
}
