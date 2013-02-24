package com.subgraph.vega.api.model.scope;

import java.net.URI;
import java.util.Collection;

import org.apache.http.HttpHost;

public interface ITargetScope {
	
	long getScopeId();
	
	/**
	 * Returns the name of this scope instance.
	 * 
	 * Use {@link #setName()} to change name.
	 * 
	 * @return The name of this scope instance.
	 */
	String getName();
	
	/**
	 * Set a new name for this scope instance.
	 * 
	 * Fires {@link ActiveScopeChangedEvent} if this is the active scope.
	 * 
	 * @param name The new name for this scope.
	 */
	void setName(String name);
	
	/**
	 * Add an URI to this scope instance.
	 * 
	 * Fires {@link ActiveScopeChangedEvent} if this is the active scope.
	 * 
	 * @param uri The URI path to add to the scope.
	 */
	void addScopeURI(URI uri);
	
	/** 
	 * Removes the specified URI from this scope instance.  If the
	 * removeContained flag is set, also remove any URIs from this
	 * scope which are contained by specified URI.  
	 * 
	 * Fires {@link ActiveScopeChangedEvent} if this is the active scope.
	 *  
	 * @param uri The URI to remove.
	 * @param removeContained Also remove any scope URIs which are contained
	 *                        by the specified URI.
	 */
	void removeScopeURI(URI uri, boolean removeContained);
	
	/**
	 * Return all URIs which have been added to this scope instance
	 * with the {@link #addScopeURI(URI)} method.
	 * 
	 * @return All URIs which have been added to this scope instance.
	 */
	Collection<URI> getScopeURIs();
	
	/**
	 * Add a regular expression pattern to the exclusion filter.
	 * 
	 * Fires {@link ActiveScopeChangedEvent} if this is the active scope.
	 * 
	 * @param pattern The regular expression to add.
	 */
	void addExclusionPattern(String pattern);
	
	/**
	 * Add an URI to the exclusion filter.
	 * 
	 * Fires {@link ActiveScopeChangedEvent} if this is the active scope.
	 * 
	 * @param uri The URI to add.
	 */
	void addExclusionURI(URI uri);
	
	/**
	 * Remove a regular expression pattern from the exclusion filter.
	 * 
	 * Fires {@link ActiveScopeChangedEvent} if this is the active scope.
	 * 
	 * @param pattern The regular expression to remove.
	 */
	void removeExclusionPattern(String pattern);
	
	/**
	 * Remove a URI from the exclusion filter.
	 * 
	 * Fires {@link ActiveScopeChangedEvent} if this is the active scope.
	 * 
	 * @param uri The URI to remove.
	 */
	void removeExclusionURI(URI uri);
	
	/**
	 * Returns a collection of all regular expressions added to the exclusion filter.
	 * 
	 * @return a collection of all regular expressions added to the exclusion filter.
	 */
	Collection<String> getExclusionPatterns();
	
	/**
	 * Returns a collection of all URIs added to the exclusion filter.
	 * 
	 * @return a collection of all URIs added to the exclusion filter.
	 */
	Collection<URI> getExclusionURIs();
	
	/**
	 * Returns true if the given URI matches the exclusion filter
	 * 
	 * @param uri The uri to test.
	 * @return true if uri matches exclusion filter.
	 */
	boolean isExcluded(URI uri);
	
	/**
	 * Returns true if the given host and uriPath pair matches
	 * the exclusion filter.
	 * 
	 * @param host The host to test.
	 * @param uriPath The uriPath to test.
	 * @return true if the host and uriPath pair matches the exclusion filter.
	 */
	boolean isExcluded(HttpHost host, String uriPath);

	/**
	 * Returns true if the given URI matches the scope filter.
	 * @param uri The uri to test
	 * @return true if the uri matches the scope filter.
	 */
	boolean isInsideScope(URI uri);
	
	/**
	 * Returns true if the given host and uriPath pair matches the 
	 * scope filter.
	 * @param host The host to test.
	 * @param uriPath The uri path to test.
	 * @return true if the host and uriPath pair matches the scope filter.
	 */
	boolean isInsideScope(HttpHost host, String uriPath);
	
	/**
	 * Returns true if the given URI matches the scope filter but
	 * does not match the exclusion filter.
	 * 
	 * Equivalent to (isInsideScope(uri) && !isExcluded(uri))
	 * 
	 * @param uri The uri to test.
	 * @return true if the given URI matches the scope filter but
	 *              does not match the exclusion filter.
	 */
	boolean filter(URI uri);

	
	/**
	 * Returns true if the given host and uriPath matches the scope filter but
	 * does not match the exclusion filter.
	 * 
	 * Equivalent to (isInsideScope(host, uriPath) && !isExcluded(host, uriPath))
	 * 
	 * @param host The host to test
	 * @param uriPath The path to test
	 * @return true if the given host and uriPath pair matches the scope filter but
	 *              does not match the exclusion filter.
	 */

	boolean filter(HttpHost host, String uriPath);
	
	/**
	 * Resets the state of this scope instance by removing all scope URIs 
	 * and exclusion patterns. 
	 */
	void clear();
	
	/**
	 * Returns true if this is the default scope.
	 * @return true if this is the default scope.
	 */
	boolean isDefaultScope();
	
	/**
	 * Returns true if this is the currently active scope.
	 * 
	 * @return true if this is the active scope.
	 */
	boolean isActiveScope();

	void setReadOnly(boolean value);
		
	boolean isReadOnly();
	
	boolean isDetached();
	
	boolean isEmpty();
}
