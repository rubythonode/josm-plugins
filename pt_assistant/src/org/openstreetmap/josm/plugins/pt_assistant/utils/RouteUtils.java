package org.openstreetmap.josm.plugins.pt_assistant.utils;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.actions.DownloadPrimitiveAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.PrimitiveId;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Utils class for routes
 * 
 * @author darya
 *
 */
public class RouteUtils {

	private RouteUtils() {
		// private constructor for util classes
	}

	/**
	 * Checks if the relation is a route of one of the following categories:
	 * bus, trolleybus, share_taxi, tram, light_rail, subway, train.
	 * 
	 * @param r
	 *            Relation to be checked
	 * @return true if the route belongs to the categories that can be validated
	 *         with the pt_assistant plugin, false otherwise.
	 */
	public static boolean isTwoDirectionRoute(Relation r) {
		if (!r.hasKey("route") || !r.hasTag("public_transport:version", "2")) {
			return false;
		}
		if (r.hasTag("route", "bus") || r.hasTag("route", "trolleybus") || r.hasTag("route", "share_taxi")
				|| r.hasTag("route", "tram") || r.hasTag("route", "light_rail") || r.hasTag("route", "subway")
				|| r.hasTag("route", "train")) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the relation member refers to a stop in a public transport
	 * route. Some stops can be modeled with ways.
	 * 
	 * @param rm
	 *            relation member to be checked
	 * @return true if the relation member refers to a stop, false otherwise
	 */
	public static boolean isPTStop(RelationMember rm) {

		if (rm.getType().equals(OsmPrimitiveType.NODE)) {
			return true;
		}

		if (rm.getType().equals(OsmPrimitiveType.RELATION)) {
			if (rm.getRole().equals("stop_area")) {
				return true;
			} else {
				return false;
			}
		}

		Way w = rm.getWay();

		if (w.hasTag("public_transport", "platform") || w.hasTag("highway", "platform")
				|| w.hasTag("railway", "platform") || w.hasTag("public_transport", "platform_entry_only")
				|| w.hasTag("highway", "platform_entry_only") || w.hasTag("railway", "platform_entry_only")
				|| w.hasTag("public_transport", "platform_exit_only") || w.hasTag("highway", "platform_exit_only")
				|| w.hasTag("railway", "platform_exit_only")) {
			return true;
		}

		return false;

	}

	/**
	 * Checks if the relation member refers to a way in a public transport
	 * route. Some OsmPrimitiveType.WAY have to be excluded because platforms
	 * can be modeled with ways.
	 * 
	 * @param rm
	 *            relation member to be checked
	 * @return true if the relation member refers to a way in a public transport
	 *         route, false otherwise.
	 */
	public static boolean isPTWay(RelationMember rm) {

		return !isPTStop(rm);
	}

	/**
	 * Checks if the type of the way is suitable for buses to go on it. The
	 * direction of the way (i.e. one-way roads) is irrelevant for this test.
	 * 
	 * TODO: this test is duplicated in WayChecker, remove it here when the old
	 * implementation is not needed anymore.
	 * 
	 * @deprecated
	 * 
	 * @param way
	 *            to be checked
	 * @return true if the way is suitable for buses, false otherwise.
	 */
	public static boolean isWaySuitableForBuses(Way way) {
		if (way.hasTag("highway", "motorway") || way.hasTag("highway", "trunk") || way.hasTag("highway", "primary")
				|| way.hasTag("highway", "secondary") || way.hasTag("highway", "tertiary")
				|| way.hasTag("highway", "unclassified") || way.hasTag("highway", "road")
				|| way.hasTag("highway", "residential") || way.hasTag("highway", "service")
				|| way.hasTag("highway", "motorway_link") || way.hasTag("highway", "trunk_link")
				|| way.hasTag("highway", "primary_link") || way.hasTag("highway", "secondary_link")
				|| way.hasTag("highway", "tertiary_link") || way.hasTag("highway", "living_street")
				|| way.hasTag("highway", "bus_guideway") || way.hasTag("highway", "road")
				|| way.hasTag("cycleway", "share_busway") || way.hasTag("cycleway", "shared_lane")) {
			return true;
		}

		return false;
	}

	public static boolean hasIncompleteMembers(Relation r) {
		if (r == null) {
			return true;
		}
		for (RelationMember rm : r.getMembers()) {
			if ((rm.isNode() && rm.getNode().isIncomplete()) || (rm.isWay() && rm.getWay().isIncomplete())
					|| (rm.isRelation() && rm.getRelation().isIncomplete())) {
				return true;
			}
		}

		return false;
	}

}