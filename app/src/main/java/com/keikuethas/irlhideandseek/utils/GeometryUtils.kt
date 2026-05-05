package com.keikuethas.irlhideandseek.utils

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt


// Vibecode повсюду
// describe параметры
val EARTH_RADIUS_METERS = 6371000.0

/**
 * Расстояние между двумя точками в метрах (Формула гаверсинусов)
 */
fun distanceBetweenPoints(point1: Point, point2: Point): Double {
    val lat1Rad = Math.toRadians(point1.latitude)
    val lat2Rad = Math.toRadians(point2.latitude)
    val deltaLat = Math.toRadians(point2.latitude - point1.latitude)
    val deltaLon = Math.toRadians(point2.longitude - point1.longitude)

    val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
            cos(lat1Rad) * cos(lat2Rad) *
            sin(deltaLon / 2) * sin(deltaLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return EARTH_RADIUS_METERS * c
}


/**
 * Проверка: находится ли точка внутри полигона (Алгоритм Ray Casting)
 */
fun isPointInsidePolygon(point: Point, polygon: Polygon): Boolean {
    val ring = polygon.outerRing
    val points = ring.points

    var isInside = false
    var j = points.size - 1

    for (i in points.indices) {
        val pi = points[i]
        val pj = points[j]

        if (((pi.latitude > point.latitude) != (pj.latitude > point.latitude)) &&
            (point.longitude < (pj.longitude - pi.longitude) *
                    (point.latitude - pi.latitude) / (pj.latitude - pi.latitude) + pi.longitude)
        ) {
            isInside = !isInside
        }
        j = i
    }

    return isInside
}


/**
 * Проверка: находится ли точка внутри круга
 */
fun isPointInCircle(point: Point, center: Point, radiusMeters: Double): Boolean =
    distanceBetweenPoints(point, center) <= radiusMeters


/**
 * Найти ближайшую точку на полилинии
 */
fun findNearestPointOnPolyline(point: Point, polyline: Polyline): Point {
    val points = polyline.points
    if (points.isEmpty()) return point
    if (points.size == 1) return points[0]

    var nearestPoint = points[0]
    var minDistance = point.distanceTo(points[0])

    for (i in 0 until points.size - 1) {
        val p1 = points[i]
        val p2 = points[i + 1]

        val projection = projectPointOnLine(point, p1, p2)
        val distance = point.distanceTo( projection)

        if (distance < minDistance) {
            minDistance = distance
            nearestPoint = projection
        }
    }

    return nearestPoint
}

/**
 * Проверка близости к линии (с допуском)
 */
fun isPointNearPolyline(point: Point, polyline: Polyline, thresholdMeters: Double): Boolean {
    val nearestPoint = findNearestPointOnPolyline(point, polyline)
    return point.distanceTo(nearestPoint) <= thresholdMeters
}

// describe
fun isPointOnLine(point: Point, lineStart: Point, lineEnd: Point) =
    point.distanceToLine(lineStart, lineEnd) == 0.0

fun isPointBetween( //vibecode
    point: Point,
    start: Point,
    end: Point,
    toleranceMeters: Double = 5.0
): Boolean {
    // 1. Проверяем, что точка в bounding box отрезка (с запасом)
    val minLat = minOf(start.latitude, end.latitude) - 0.0001
    val maxLat = maxOf(start.latitude, end.latitude) + 0.0001
    val minLon = minOf(start.longitude, end.longitude) - 0.0001
    val maxLon = maxOf(start.longitude, end.longitude) + 0.0001

    if (point.latitude !in minLat..maxLat || point.longitude !in minLon..maxLon) {
        return false
    }

    // 2. Проверяем расстояние от точки до отрезка
    val distance = point.distanceToLine(start, end)
    return distance <= toleranceMeters
}

/**
 * Лежит ли точка линии на данном отрезке (не выполняется проверка на расстояние)
 */
fun isProjectionBetween( //по аналогии с vibecode
    point: Point,
    start: Point,
    end: Point,
    toleranceMeters: Double = 5.0
): Boolean {
    // 1. Проверяем, что точка в bounding box отрезка (с запасом)
    val minLat = minOf(start.latitude, end.latitude) - 0.0001
    val maxLat = maxOf(start.latitude, end.latitude) + 0.0001
    val minLon = minOf(start.longitude, end.longitude) - 0.0001
    val maxLon = maxOf(start.longitude, end.longitude) + 0.0001

    return (point.latitude in minLat..maxLat && point.longitude in minLon..maxLon)
}

fun pointToLineDistance(point: Point, start: Point, end: Point) =
    point.distanceTo(point.projectOnLine(start, end))

/**
 * Проекция точки на отрезок
 */
fun projectPointOnLine(point: Point, lineStart: Point, lineEnd: Point): Point {
    val dx = lineEnd.longitude - lineStart.longitude
    val dy = lineEnd.latitude - lineStart.latitude

    if (dx == 0.0 && dy == 0.0) return lineStart

    val t = ((point.longitude - lineStart.longitude) * dx +
            (point.latitude - lineStart.latitude) * dy) /
            (dx * dx + dy * dy)

    val clampedT = t.coerceIn(0.0, 1.0)

    return Point(
        lineStart.latitude + clampedT * dy,
        lineStart.longitude + clampedT * dx
    )
}


// --- extensions --- describe
fun Point.distanceTo(point2: Point) = distanceBetweenPoints(this, point2)
////fun Point.distanceTo(point2: Point) =
//    sqrt((this.latitude - point2.latitude).pow(2) + (this.longitude - point2.longitude).pow(2))
fun Point.projectOnLine(lineStart: Point, lineEnd: Point) =
    projectPointOnLine(this, lineStart, lineEnd)

fun Point.isOnLine(lineStart: Point, lineEnd: Point) = isPointOnLine(this, lineStart, lineEnd)
fun Point.isInsideCircle(center: Point, radiusMeters: Double) =
    isPointInCircle(this, center, radiusMeters)

fun Point.isInsidePolygon(polygon: Polygon) = isPointInsidePolygon(this, polygon)

fun Point.isBetween(start: Point, end: Point) = isPointBetween(this, start, end)

fun Point.distanceToLine(start: Point, end: Point) = pointToLineDistance(this, start, end)

fun Point.projectionBetween(start: Point, end: Point, toleranceMeters: Double = 5.0) =
    isProjectionBetween(this, start, end)

fun Point.distanceToSegment(start: Point, end: Point): Double =
    this.distanceToLine(start, end) + min(this.distanceTo(start), this.distanceTo(end))