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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;

 public class AnnotationPainter
   implements IPainter, PaintListener, ISelectionProvider
 {
   private Set<ISelectionChangedListener> listeners = new HashSet<ISelectionChangedListener>();
   private SourceViewer viewer;
   private Map<ISelfDrawingAnnotation, Position> anns = new HashMap<ISelfDrawingAnnotation, Position>();
 
   public AnnotationPainter(SourceViewer v)
   {
     this.viewer = v;
     this.viewer.getTextWidget().addPaintListener(this);
   }
 
   public void addSelectionChangedListener(ISelectionChangedListener listener) {
     this.listeners.add(listener);
   }
 
   public void removeSelectionChangedListener(ISelectionChangedListener listener) {
     this.listeners.remove(listener);
   }
 
   private void fireSelectionEvent() {
     SelectionChangedEvent e = new SelectionChangedEvent(this, getSelection());
     for (ISelectionChangedListener l : this.listeners)
       l.selectionChanged(e);
   }
 
   public Position getPosition(ISelfDrawingAnnotation annotation)
   {
     return (Position)this.anns.get(annotation);
   }
 
   /** @deprecated */
   public void replaceAnnotations(ISelfDrawingAnnotation[] remove, Map<ISelfDrawingAnnotation, Position> add)
   {
     List<Position> positions = new ArrayList<Position>();
 
     if (remove != null) {
       for (ISelfDrawingAnnotation r : remove) {
         Position p = (Position)this.anns.remove(r);
         if (p != null)
           positions.add(p);
       }
     }
     if (add != null) {
       this.anns.putAll(add);
       positions.addAll(add.values());
     }
 
     fireAnnotationChangedEvent(positions);
   }
 
   public void replaceAnnotations(AnnTransaction trans) {
     trans.replaceAnnotations(this);
   }
 
   public void addAnnotation(ISelfDrawingAnnotation a, Position p)
   {
     this.anns.put(a, p);
     List<Position> positions = new ArrayList<Position>(1);
     positions.add(p);
     fireAnnotationChangedEvent(positions);
   }
 
   public void removeAnnotation(ISelfDrawingAnnotation ann)
   {
     Position position = (Position)this.anns.remove(ann);
 
     if (position != null) {
       List<Position> positions = new ArrayList<Position>(1);
       positions.add(position);
       fireAnnotationChangedEvent(positions);
     }
   }
 
   public void removeAllAnnotations() {
     Collection<Position> positions = new ArrayList<Position>(this.anns.values());
     this.anns.clear();
     fireAnnotationChangedEvent(positions);
   }
 
   private void fireAnnotationChangedEvent(Collection<Position> positions)
   {
     if (positions.isEmpty()) {
       return;
     }
     int start = Integer.MAX_VALUE;
     int end = Integer.MIN_VALUE;
     
 
     for (Position p : positions) {
       if (start > p.getOffset())
         start = p.offset;
       int tempEnd = p.getOffset() + p.getLength();
       if (end < tempEnd) {
         end = tempEnd;
       }
     }
     start = widgetIndex(start);
     end = widgetIndex(end);
     this.viewer.getTextWidget().redrawRange(start, end - start, false);
   }
   public void deactivate(boolean redraw) {
   }
   public void setPositionManager(IPaintPositionManager manager) {
   }
 
   public void paint(int reason) {
     switch (reason) {
     case IPainter.SELECTION:
     case IPainter.TEXT_CHANGE:
     case IPainter.KEY_STROKE:
     case IPainter.MOUSE_BUTTON:
       fireSelectionEvent();
     case IPainter.KEY_STROKE | IPainter.TEXT_CHANGE:
     }
   }
 
   public void dispose() {
     refresh();
 
     this.anns.clear();
     paint(IPainter.INTERNAL);
 
     this.viewer.getTextWidget().removePaintListener(this);
     this.viewer.removePainter(this);
   }
 
   public void paintControl(PaintEvent e)
   {
     for (Map.Entry<ISelfDrawingAnnotation, Position> entry : this.anns.entrySet()) {
       ISelfDrawingAnnotation ann = (ISelfDrawingAnnotation)entry.getKey();
       Position p = (Position)entry.getValue();
       IRegion r = this.viewer.modelRange2WidgetRange(new Region(p.offset, p.length));
 
       if (r != null)
         ann.draw(e.gc, this.viewer.getTextWidget(), 
           r.getOffset(), 
           r.getLength());
     }
   }
 
   private int widgetIndex(int offset) {
     int index = this.viewer.modelOffset2WidgetOffset(offset);
     if (index < 0)
       index = this.viewer.getBottomIndexEndOffset();
     return index;
   }
 
   public ITextSelection getSelection() {
     return (ITextSelection)this.viewer.getSelection();
   }
 
   public void setSelection(ISelection selection) {
     this.viewer.setSelection(selection);
   }
 
   public Iterator<ISelfDrawingAnnotation> getAnnotationIterator()
   {
     return this.anns.keySet().iterator();
   }
 
   private void refresh() {
     fireAnnotationChangedEvent(this.anns.values());
   }
 
   public void refresh(Collection<ISelfDrawingAnnotation> someAnnotations) {
     List<Position> positions = new ArrayList<Position>();
     for (ISelfDrawingAnnotation ann : someAnnotations) {
       positions.add((Position)this.anns.get(ann));
     }
     fireAnnotationChangedEvent(positions);
   }
 }
