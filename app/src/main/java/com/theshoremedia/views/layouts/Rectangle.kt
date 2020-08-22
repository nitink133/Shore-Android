package com.theshoremedia.views.layouts

/**
 * @author- Nitin Khanna
 * @date -
 */

class Rectangle(val x: Double, val y: Double, val w: Double, val h: Double) {
    private val OUT_LEFT = 1
    private val OUT_TOP = 2
    private val OUT_RIGHT = 4
    private val OUT_BOTTOM = 8

    fun outcode(x: Double, y: Double): Int {
        var out = 0

        when {
            w <= 0 -> out = out or (OUT_LEFT or OUT_RIGHT)
            x < this.x -> out = out or OUT_LEFT
            x > this.x + w -> out = out or OUT_RIGHT
        }
        when {
            h <= 0 -> out = out or (OUT_TOP or OUT_BOTTOM)
            y < this.y -> out = out or OUT_TOP
            y > this.y + h -> out = out or OUT_BOTTOM
        }
        return out
    }

    fun intersectsLine(x1: Double, y1: Double, x2: Double, y2: Double): Boolean {
        var x1 = x1
        var y1 = y1
        var out1: Int
        val out2: Int = outcode(x2, y2)
        if (out2 == 0) {
            return true
        }
        do {
            out1 = outcode(x1, y1)

            if (out1 == 0) break

            if (out1 and out2 != 0) {
                return false
            }
            if (out1 and (OUT_LEFT or OUT_RIGHT) != 0) {
                var x = x
                if (out1 and OUT_RIGHT != 0) {
                    x += w
                }
                y1 += (x - x1) * (y2 - y1) / (x2 - x1)
                x1 = x
            } else {
                var y = y
                if (out1 and OUT_BOTTOM != 0) {
                    y += h
                }
                x1 += (y - y1) * (x2 - x1) / (y2 - y1)
                y1 = y
            }
        } while (true)

        return true
    }
}
