package app.net.request.thing;

import java.util.List;
import java.util.function.Function;

/**
 * <h1>Data</h1>
 *
 * @author Dan Ottosson
 */

public record Data (String after, String modhash, String geo_filter, List<Child> children) {
}
