/*
 *
 *     Copyright (C) 2019  Salkin (mc.salkin@gmail.com)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.settla.spigot.universe;

import de.settla.memory.MemoryName;
import de.settla.memory.MemoryStorable;

import com.google.gson.JsonObject;

@MemoryName("Vector")
public class Vector implements Comparable<Vector>, MemoryStorable<Vector> {

    public static final Vector ZERO = new Vector(0, 0, 0);
    public static final Vector UNIT_X = new Vector(1, 0, 0);
    public static final Vector UNIT_Y = new Vector(0, 1, 0);
    public static final Vector UNIT_Z = new Vector(0, 0, 1);
    public static final Vector ONE = new Vector(1, 1, 1);

    protected final double x, y, z;

    public Vector(JsonObject json) {
        x = json.get("x").getAsDouble();
        y = json.get("y").getAsDouble();
        z = json.get("z").getAsDouble();
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(int x, int y, int z) {
        this.x = (double) x;
        this.y = (double) y;
        this.z = (double) z;
    }

    public Vector(Vector other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public static Vector getMinimum(Vector v1, Vector v2) {
        return new Vector(
                Math.min(v1.x, v2.x),
                Math.min(v1.y, v2.y),
                Math.min(v1.z, v2.z)
        );
    }

    public static Vector getMaximum(Vector v1, Vector v2) {
        return new Vector(
                Math.max(v1.x, v2.x),
                Math.max(v1.y, v2.y),
                Math.max(v1.z, v2.z)
        );
    }

    public static Vector getMidpoint(Vector v1, Vector v2) {
        return new Vector(
                (v1.x + v2.x) / 2.0,
                (v1.y + v2.y) / 2.0,
                (v1.z + v2.z) / 2.0
        );
    }

    public static boolean isSmaller(Vector v1, Vector v2) {
    	return v1.getX() < v2.getX() && v1.getY() < v2.getY() && v1.getZ() < v2.getZ();
    }
    
    public static boolean isSmallerOrEqual(Vector v1, Vector v2) {
    	return v1.getX() <= v2.getX() && v1.getY() <= v2.getY() && v1.getZ() <= v2.getZ();
    }
    
    public static boolean isEqual(Vector v1, Vector v2) {
    	return v1.getX() == v2.getX() && v1.getY() == v2.getY() && v1.getZ() == v2.getZ();
    }
    
    @Override
    public JsonObject serialize() {
        JsonObject json = MemoryStorable.super.serialize();
        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("z", z);
        return json;
    }

    public double getX() {
        return x;
    }

    public Vector setX(double x) {
        return new Vector(x, y, z);
    }

    public Vector setX(int x) {
        return new Vector(x, y, z);
    }

    public double getY() {
        return y;
    }

    public Vector setY(double y) {
        return new Vector(x, y, z);
    }

    public Vector setY(int y) {
        return new Vector(x, y, z);
    }

    public double getZ() {
        return z;
    }

    public Vector setZ(double z) {
        return new Vector(x, y, z);
    }

    public Vector setZ(int z) {
        return new Vector(x, y, z);
    }

    public int getBlockX() {
        return (int) Math.round(x);
    }

    public int getBlockY() {
        return (int) Math.round(y);
    }

    public int getBlockZ() {
        return (int) Math.round(z);
    }

    public Vector add(Vector other) {
        return new Vector(x + other.x, y + other.y, z + other.z);
    }

    public Vector add(double x, double y, double z) {
        return new Vector(this.x + x, this.y + y, this.z + z);
    }

    public Vector add(int x, int y, int z) {
        return new Vector(this.x + x, this.y + y, this.z + z);
    }

    public Vector add(Vector... others) {
        double newX = x, newY = y, newZ = z;

        for (Vector other : others) {
            newX += other.x;
            newY += other.y;
            newZ += other.z;
        }

        return new Vector(newX, newY, newZ);
    }

    public Vector subtract(Vector other) {
        return new Vector(x - other.x, y - other.y, z - other.z);
    }

    public Vector subtract(double x, double y, double z) {
        return new Vector(this.x - x, this.y - y, this.z - z);
    }

    public Vector subtract(int x, int y, int z) {
        return new Vector(this.x - x, this.y - y, this.z - z);
    }

    public Vector subtract(Vector... others) {
        double newX = x, newY = y, newZ = z;
        for (Vector other : others) {
            newX -= other.x;
            newY -= other.y;
            newZ -= other.z;
        }
        return new Vector(newX, newY, newZ);
    }

    public Vector multiply(Vector other) {
        return new Vector(x * other.x, y * other.y, z * other.z);
    }

    public Vector multiply(double x, double y, double z) {
        return new Vector(this.x * x, this.y * y, this.z * z);
    }

    public Vector multiply(int x, int y, int z) {
        return new Vector(this.x * x, this.y * y, this.z * z);
    }

    public Vector multiply(Vector... others) {
        double newX = x, newY = y, newZ = z;
        for (Vector other : others) {
            newX *= other.x;
            newY *= other.y;
            newZ *= other.z;
        }
        return new Vector(newX, newY, newZ);
    }

    public Vector multiply(double n) {
        return new Vector(this.x * n, this.y * n, this.z * n);
    }

    public Vector multiply(float n) {
        return new Vector(this.x * n, this.y * n, this.z * n);
    }

    public Vector multiply(int n) {
        return new Vector(this.x * n, this.y * n, this.z * n);
    }

    public Vector divide(Vector other) {
        return new Vector(x / other.x, y / other.y, z / other.z);
    }

    public Vector divide(double x, double y, double z) {
        return new Vector(this.x / x, this.y / y, this.z / z);
    }

    public Vector divide(int x, int y, int z) {
        return new Vector(this.x / x, this.y / y, this.z / z);
    }

    public Vector divide(int n) {
        return new Vector(x / n, y / n, z / n);
    }

    public Vector divide(double n) {
        return new Vector(x / n, y / n, z / n);
    }

    public Vector divide(float n) {
        return new Vector(x / n, y / n, z / n);
    }

    public double lengthSq() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(lengthSq());
    }

    public double distanceSq(Vector other) {
        return Math.pow(other.x - x, 2) +
                Math.pow(other.y - y, 2) +
                Math.pow(other.z - z, 2);
    }

    public double distance(Vector other) {
        return Math.sqrt(distanceSq(other));
    }

    public Vector normalize() {
        return divide(length());
    }

    public double dot(Vector other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector cross(Vector other) {
        return new Vector(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    public Vector floor() {
        return new Vector(Math.floor(x), Math.floor(y), Math.floor(z));
    }

    public Vector ceil() {
        return new Vector(Math.ceil(x), Math.ceil(y), Math.ceil(z));
    }

    public Vector round() {
        return new Vector(Math.floor(x + 0.5), Math.floor(y + 0.5), Math.floor(z + 0.5));
    }

    public Vector positive() {
        return new Vector(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public float toPitch() {
        double x = getX();
        double z = getZ();

        if (x == 0 && z == 0) {
            return getY() > 0 ? -90 : 90;
        } else {
            double x2 = x * x;
            double z2 = z * z;
            double xz = Math.sqrt(x2 + z2);
            return (float) Math.toDegrees(Math.atan(-getY() / xz));
        }
    }

    public float toYaw() {
        double x = getX();
        double z = getZ();

        double t = Math.atan2(-x, z);
        double _2pi = 2 * Math.PI;

        return (float) Math.toDegrees(((t + _2pi) % _2pi));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector)) {
            return false;
        }

        Vector other = (Vector) obj;
        return other.x == this.x && other.y == this.y && other.z == this.z;
    }

    @Override
    public int compareTo(Vector other) {
        if (other == null) {
            throw new IllegalArgumentException("null not supported");
        }
        if (y != other.y) return Double.compare(y, other.y);
        if (z != other.z) return Double.compare(z, other.z);
        if (x != other.x) return Double.compare(x, other.x);
        return 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

}
