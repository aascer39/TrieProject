package org.example;

import java.util.Objects;

public class NodePosition {
    private final double x;
    private final double y;

    public NodePosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 检查两条线段是否交叉
     * @param a1 起点1
     * @param a2 终点1
     * @param b1 起点2
     * @param b2 终点2
     * @return 是否交叉
     */
    public static boolean isIntersecting(NodePosition a1, NodePosition a2, NodePosition b1, NodePosition b2) {
        // 计算叉积
        double cross1 = crossProduct(a1, a2, b1);  // 向量 (a1 -> a2) 与 向量 (a1 -> b1)
        double cross2 = crossProduct(a1, a2, b2);  // 向量 (a1 -> a2) 与 向量 (a1 -> b2)
        double cross3 = crossProduct(b1, b2, a1);  // 向量 (b1 -> b2) 与 向量 (b1 -> a1)
        double cross4 = crossProduct(b1, b2, a2);  // 向量 (b1 -> b2) 与 向量 (b1 -> a2)

        // 判断线段是否相交
        return (cross1 * cross2 < 0) && (cross3 * cross4 < 0);
    }

    // 计算叉积
    private static double crossProduct(NodePosition p1, NodePosition p2, NodePosition p3) {
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) - (p2.getY() - p1.getY()) * (p3.getX() - p1.getX());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NodePosition that = (NodePosition) obj;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
