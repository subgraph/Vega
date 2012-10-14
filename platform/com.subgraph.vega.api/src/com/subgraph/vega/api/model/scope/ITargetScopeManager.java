package com.subgraph.vega.api.model.scope;

import java.util.Collection;

import com.subgraph.vega.api.events.IEventHandler;

public interface ITargetScopeManager {
	/**
	 * Create and return a new empty scope instance.  The returned
	 * scope will not be saved persistently in the database until the
	 * {@link #saveScope(ITargetScope)} method is called.
	 * 
	 * @return The new scope instance.
	 */
	ITargetScope createNewScope();
	
	/**
	 * Create and return a new scope instance which is a duplicate of
	 * the scope parameter.
	 * 
	 * @param scope The scope instance to duplicate.
	 * @return A new scope instance which is a duplicate of the scope parameter.
	 */
	ITargetScope createDuplicatedScope(ITargetScope scope);
	
	/**
	 * Remove the specified scope instance from the manager.  The
	 * default scope cannot be removed.  This method will return 
	 * false if passed the default scope or a scope instance not
	 * recognized by the manager.  If the currently active scope
	 * is removed, the default scope will become the active scope.
	 * 
	 * @param scope The scope to remove.
	 * @return true if the scope was successfully removed.
	 */
	boolean removeScope(ITargetScope scope);
	
	/**
	 * Save this scope in the database.
	 * 
	 * @param scope The scope to save.
	 */
	void saveScope(ITargetScope scope);
	
	/**
	 * Return all persistent scope instances from the manager.
	 * 
	 * @return All scope instances.
	 */
	Collection<ITargetScope> getAllScopes();
	
	/** 
	 * Return the currently active scope.  There is always a single active
	 * scope.  Use {@link #setActiveScope(ITargetScope)} to change the
	 * active scope.
	 * 
	 * @return The currently active scope.
	 */
	ITargetScope getActiveScope();
	
	/**
	 * Return the default scope.  The default scope is created automatically
	 * for a workspace and cannot be removed.
	 * @return
	 */
	ITargetScope getDefaultScope();
	
	/**
	 * Set the active scope to the specified scope instance.
	 * 
	 * @param scope The scope to set as the active scope.
	 */
	void setActiveScope(ITargetScope scope);
	
	/**
	 * Add a listener for the event {@link ActiveScopeChangedEvent}.  This event
	 * is fired when the currently active scope is changed to another scope or the
	 * properties of the active scope are modified.
	 * 
	 * @param listener The event handler to register.
	 * @return The currently active target scope
	 */
	ITargetScope addActiveScopeChangeListener(IEventHandler listener);
	
	/**
	 * Removes an event handler registered with {@link #addActiveScopeChangeListener(IEventHandler)}.
	 * 
	 * @param listener The event handler to remove.
	 */
	void removeActiveScopeChangeListener(IEventHandler listener);
	
	void setScopeDetached(ITargetScope scope);
	void setScopeAttached(ITargetScope scope);
}
