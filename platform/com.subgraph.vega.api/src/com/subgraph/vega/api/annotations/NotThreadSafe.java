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
package com.subgraph.vega.api.annotations;

import java.lang.annotation.*;                                                                                                                                                                                                              

/*                                                                                                                                                                                                                                          
 * Copyright (c) 2005 Brian Goetz and Tim Peierls                                                                                                                                                                                           
 * Released under the Creative Commons Attribution License                                                                                                                                                                                  
 *   (http://creativecommons.org/licenses/by/2.5)                                                                                                                                                                                           
 * Official home: http://www.jcip.net                                                                                                                                                                                                       
 *                                                                                                                                                                                                                                          
 * Any republication or derived work distributed in source code form                                                                                                                                                                        
 * must include this copyright and license notice.                                                                                                                                                                                          
 */                                                                                                                                                                                                                                         
                                                                                                                                                                                                                                            
                                                                                                                                                                                                                                            
/**                                                                                                                                                                                                                                         
 * The class to which this annotation is applied is not thread-safe.                                                                                                                                                                        
 * This annotation primarily exists for clarifying the non-thread-safety of a class                                                                                                                                                         
 * that might otherwise be assumed to be thread-safe, despite the fact that it is a bad                                                                                                                                                     
 * idea to assume a class is thread-safe without good reason.                                                                                                                                                                               
 * @see ThreadSafe                                                                                                                                                                                                                          
 */                                                                                                                                                                                                                                         
@Documented                                                                                                                                                                                                                                 
@Target(ElementType.TYPE)                                                                                                                                                                                                                   
@Retention(RetentionPolicy.RUNTIME)                                                                                                                                                                                                         
public @interface NotThreadSafe {                                                                                                                                                                                                           
}                                  
