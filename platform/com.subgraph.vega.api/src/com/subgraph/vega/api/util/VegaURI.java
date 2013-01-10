package com.subgraph.vega.api.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.utils.URIUtils;

public class VegaURI {
	
	public static VegaURI fromHostAndRequest(HttpHost targetHost, HttpRequest request) {
		final String line = request.getRequestLine().getUri();
		return new VegaURI(targetHost, lineToPath(line), lineToQuery(line));
	}

	private static String lineToPath(String line) {
		final int idx = line.indexOf('?');
		if(idx == -1) {
			return line;
		}
		return line.substring(0, idx);
	}
	
	private static String lineToQuery(String line) {
		final int idx = line.indexOf('?');
		if(idx == -1) {
			return null;
		}
		return line.substring(idx + 1);
	}

	private final HttpHost targetHost;
	private final String path;
	private final String query;
	
	public VegaURI(HttpHost targetHost, String path, String query) {
		this.targetHost = targetHost;
		this.path = path;
		this.query = query;
	}
	
	public HttpHost getTargetHost() {
		return targetHost;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getQuery() {
		return query;
	}

	public URI toURI() {
		try {
			final URI pathURI = new URI(null, null, null, -1, path, query, null);
			return URIUtils.rewriteURI(pathURI, targetHost);
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Failed to convert VegaURI to URI: "+ toString(), e);
		}
	}

	public VegaURI resolve(String uriLine) {
		final String newPath = lineToPath(uriLine);
		final String newQuery = lineToQuery(uriLine);
		
		if(newPath.startsWith("/")) {
			return new VegaURI(targetHost, newPath, newQuery);
		} else {
			return new VegaURI(targetHost, resolvePath(path, newPath, true), newQuery);
		}
	}

	// -- Normalization, resolution, and relativization --

    // RFC2396 5.2 (6)
    private static String resolvePath(String base, String child,
                                      boolean absolute)
    {
        int i = base.lastIndexOf('/');
        int cn = child.length();
        String path = "";

        if (cn == 0) {
            // 5.2 (6a)
            if (i >= 0)
                path = base.substring(0, i + 1);
        } else {
            StringBuffer sb = new StringBuffer(base.length() + cn);
            // 5.2 (6a)
            if (i >= 0)
                sb.append(base.substring(0, i + 1));
            // 5.2 (6b)
            sb.append(child);
            path = sb.toString();
        }

        // 5.2 (6c-f)
        String np = normalize(path);

        // 5.2 (6g): If the result is absolute but the path begins with "../",
        // then we simply leave the path as-is

        return np;
    }
    
      // Normalize the given path string.  A normal path string has no empty
    // segments (i.e., occurrences of "//"), no segments equal to ".", and no
    // segments equal to ".." that are preceded by a segment not equal to "..".
    // In contrast to Unix-style pathname normalization, for URI paths we
    // always retain trailing slashes.
    //
    private static String normalize(String ps) {

        // Does this path need normalization?
        int ns = needsNormalization(ps);        // Number of segments
        if (ns < 0)
            // Nope -- just return it
            return ps;

        char[] path = ps.toCharArray();         // Path in char-array form

        // Split path into segments
        int[] segs = new int[ns];               // Segment-index array
        split(path, segs);

        // Remove dots
        removeDots(path, segs);

        // Prevent scheme-name confusion
        maybeAddLeadingDot(path, segs);

        // Join the remaining segments and return the result
        String s = new String(path, 0, join(path, segs));
        if (s.equals(ps)) {
            // string was already normalized
            return ps;
        }
        return s;
    }
    
    // -- Path normalization --

    // The following algorithm for path normalization avoids the creation of a
    // string object for each segment, as well as the use of a string buffer to
    // compute the final result, by using a single char array and editing it in
    // place.  The array is first split into segments, replacing each slash
    // with '\0' and creating a segment-index array, each element of which is
    // the index of the first char in the corresponding segment.  We then walk
    // through both arrays, removing ".", "..", and other segments as necessary
    // by setting their entries in the index array to -1.  Finally, the two
    // arrays are used to rejoin the segments and compute the final result.
    //
    // This code is based upon src/solaris/native/java/io/canonicalize_md.c


    // Check the given path to see if it might need normalization.  A path
    // might need normalization if it contains duplicate slashes, a "."
    // segment, or a ".." segment.  Return -1 if no further normalization is
    // possible, otherwise return the number of segments found.
    //
    // This method takes a string argument rather than a char array so that
    // this test can be performed without invoking path.toCharArray().
    //
    static private int needsNormalization(String path) {
        boolean normal = true;
        int ns = 0;                     // Number of segments
        int end = path.length() - 1;    // Index of last char in path
        int p = 0;                      // Index of next char in path

        // Skip initial slashes
        while (p <= end) {
            if (path.charAt(p) != '/') break;
            p++;
        }
        if (p > 1) normal = false;

        // Scan segments
        while (p <= end) {

            // Looking at "." or ".." ?
            if ((path.charAt(p) == '.')
                && ((p == end)
                    || ((path.charAt(p + 1) == '/')
                        || ((path.charAt(p + 1) == '.')
                            && ((p + 1 == end)
                                || (path.charAt(p + 2) == '/')))))) {
                normal = false;
            }
            ns++;

            // Find beginning of next segment
            while (p <= end) {
                if (path.charAt(p++) != '/')
                    continue;

                // Skip redundant slashes
                while (p <= end) {
                    if (path.charAt(p) != '/') break;
                    normal = false;
                    p++;
                }

                break;
            }
        }

        return normal ? -1 : ns;
    }


    // Split the given path into segments, replacing slashes with nulls and
    // filling in the given segment-index array.
    //
    // Preconditions:
    //   segs.length == Number of segments in path
    //
    // Postconditions:
    //   All slashes in path replaced by '\0'
    //   segs[i] == Index of first char in segment i (0 <= i < segs.length)
    //
    static private void split(char[] path, int[] segs) {
        int end = path.length - 1;      // Index of last char in path
        int p = 0;                      // Index of next char in path
        int i = 0;                      // Index of current segment

        // Skip initial slashes
        while (p <= end) {
            if (path[p] != '/') break;
            path[p] = '\0';
            p++;
        }

        while (p <= end) {

            // Note start of segment
            segs[i++] = p++;

            // Find beginning of next segment
            while (p <= end) {
                if (path[p++] != '/')
                    continue;
                path[p - 1] = '\0';

                // Skip redundant slashes
                while (p <= end) {
                    if (path[p] != '/') break;
                    path[p++] = '\0';
                }
                break;
            }
        }

        if (i != segs.length)
            throw new InternalError();  // ASSERT
    }


    // Join the segments in the given path according to the given segment-index
    // array, ignoring those segments whose index entries have been set to -1,
    // and inserting slashes as needed.  Return the length of the resulting
    // path.
    //
    // Preconditions:
    //   segs[i] == -1 implies segment i is to be ignored
    //   path computed by split, as above, with '\0' having replaced '/'
    //
    // Postconditions:
    //   path[0] .. path[return value] == Resulting path
    //
    static private int join(char[] path, int[] segs) {
        int ns = segs.length;           // Number of segments
        int end = path.length - 1;      // Index of last char in path
        int p = 0;                      // Index of next path char to write

        if (path[p] == '\0') {
            // Restore initial slash for absolute paths
            path[p++] = '/';
        }

        for (int i = 0; i < ns; i++) {
            int q = segs[i];            // Current segment
            if (q == -1)
                // Ignore this segment
                continue;

            if (p == q) {
                // We're already at this segment, so just skip to its end
                while ((p <= end) && (path[p] != '\0'))
                    p++;
                if (p <= end) {
                    // Preserve trailing slash
                    path[p++] = '/';
                }
            } else if (p < q) {
                // Copy q down to p
                while ((q <= end) && (path[q] != '\0'))
                    path[p++] = path[q++];
                if (q <= end) {
                    // Preserve trailing slash
                    path[p++] = '/';
                }
            } else
                throw new InternalError(); // ASSERT false
        }

        return p;
    }


    // Remove "." segments from the given path, and remove segment pairs
    // consisting of a non-".." segment followed by a ".." segment.
    //
    private static void removeDots(char[] path, int[] segs) {
        int ns = segs.length;
        int end = path.length - 1;

        for (int i = 0; i < ns; i++) {
            int dots = 0;               // Number of dots found (0, 1, or 2)

            // Find next occurrence of "." or ".."
            do {
                int p = segs[i];
                if (path[p] == '.') {
                    if (p == end) {
                        dots = 1;
                        break;
                    } else if (path[p + 1] == '\0') {
                        dots = 1;
                        break;
                    } else if ((path[p + 1] == '.')
                               && ((p + 1 == end)
                                   || (path[p + 2] == '\0'))) {
                        dots = 2;
                        break;
                    }
                }
                i++;
            } while (i < ns);
            if ((i > ns) || (dots == 0))
                break;

            if (dots == 1) {
                // Remove this occurrence of "."
                segs[i] = -1;
            } else {
                // If there is a preceding non-".." segment, remove both that
                // segment and this occurrence of ".."; otherwise, leave this
                // ".." segment as-is.
                int j;
                for (j = i - 1; j >= 0; j--) {
                    if (segs[j] != -1) break;
                }
                if (j >= 0) {
                    int q = segs[j];
                    if (!((path[q] == '.')
                          && (path[q + 1] == '.')
                          && (path[q + 2] == '\0'))) {
                        segs[i] = -1;
                        segs[j] = -1;
                    }
                }
            }
        }
    }


    // DEVIATION: If the normalized path is relative, and if the first
    // segment could be parsed as a scheme name, then prepend a "." segment
    //
    private static void maybeAddLeadingDot(char[] path, int[] segs) {

        if (path[0] == '\0')
            // The path is absolute
            return;

        int ns = segs.length;
        int f = 0;                      // Index of first segment
        while (f < ns) {
            if (segs[f] >= 0)
                break;
            f++;
        }
        if ((f >= ns) || (f == 0))
            // The path is empty, or else the original first segment survived,
            // in which case we already know that no leading "." is needed
            return;

        int p = segs[f];
        while ((p < path.length) && (path[p] != ':') && (path[p] != '\0')) p++;
        if (p >= path.length || path[p] == '\0')
            // No colon in first segment, so no "." needed
            return;

        // At this point we know that the first segment is unused,
        // hence we can insert a "." segment at that position
        path[0] = '.';
        path[1] = '\0';
        segs[0] = 0;
    }


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(targetHost.toURI());
		if(!path.startsWith("/")) {
			sb.append("/");
		}
		sb.append(path);
		if(query != null) {
			sb.append("?");
			sb.append(query);
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result
				+ ((targetHost == null) ? 0 : targetHost.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VegaURI other = (VegaURI) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (targetHost == null) {
			if (other.targetHost != null)
				return false;
		} else if (!targetHost.equals(other.targetHost))
			return false;
		return true;
	}
}
