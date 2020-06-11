package com.alvarlagerlof.quake2;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class MathUtil {

    public MathUtil() {
    }

    public Boolean intersectsRay(Location origin, Vector direction, BoundingBox box) {
        float t = 0f;

        float t1 = ((float) box.getMinX() - (float) origin.getX()) / (float) direction.getX();
        float t2 = ((float) box.getMaxX() - (float) origin.getX()) / (float) direction.getX();
        float t3 = ((float) box.getMinY() - (float) origin.getY()) / (float) direction.getY();
        float t4 = ((float) box.getMaxY() - (float) origin.getY()) / (float) direction.getY();
        float t5 = ((float) box.getMinZ() - (float) origin.getZ()) / (float) direction.getZ();
        float t6 = ((float) box.getMaxZ() - (float) origin.getZ()) / (float) direction.getZ();

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        // if tmax < 0, ray (line) is intersecting AABB, but the whole AABB is behind us
        if (tmax < 0) {
            t = tmax;
            return false;
        }

        // if tmin > tmax, ray doesn't intersect AABB
        if (tmin > tmax) {
            t = tmax;
            return false;
        }

        // if tmin < 0 then the ray origin is inside of the AABB and tmin is behind the
        // start of the ray so tmax is the first intersection
        if (tmin < 0) {
            t = tmax;
        } else {
            t = tmin;
        }
        return true;
    }
}