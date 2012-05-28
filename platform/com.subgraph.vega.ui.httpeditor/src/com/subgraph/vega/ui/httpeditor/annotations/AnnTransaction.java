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
package com.subgraph.vega.ui.httpeditor.annotations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.Position;

public class AnnTransaction
{
   private Map<ISelfDrawingAnnotation, Position> toAdd = new HashMap<ISelfDrawingAnnotation, Position>();
 
   private List<ISelfDrawingAnnotation> toRemove = new LinkedList<ISelfDrawingAnnotation>();
 
   public void add(ISelfDrawingAnnotation a, Position p) {
     this.toAdd.put(a, p);
   }
 
   public void remove(ISelfDrawingAnnotation a) {
     this.toRemove.add(a);
   }
 
   @SuppressWarnings("deprecation")
   public void replaceAnnotations(AnnotationPainter painter) {
     ISelfDrawingAnnotation[] toRemoveArray = 
       new ISelfDrawingAnnotation[this.toRemove.size()];
 
     for (int i = 0; i < toRemoveArray.length; i++) {
       toRemoveArray[i] = ((ISelfDrawingAnnotation)this.toRemove.get(i));
     }
     painter.replaceAnnotations(toRemoveArray, this.toAdd);
   }
 }
