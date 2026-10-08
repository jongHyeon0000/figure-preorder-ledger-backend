package com.jong.figurepreorderledgerbackend.common.constant;

public enum FigureStatus {

    CART,
    UNPURCHASED,
    RESERVED,
    PURCHASED,
    DELIVERED;

    /*
     * 허용되는 전이만 true.
     * CART → UNPURCHASED → (RESERVED) → PURCHASED → DELIVERED, 되돌리기는 없다.
     */
    public boolean canMoveTo(FigureStatus next) {
        switch (this) {
            case CART:
                return next == UNPURCHASED;
            case UNPURCHASED:
                return next == RESERVED || next == PURCHASED;
            case RESERVED:
                return next == PURCHASED;
            case PURCHASED:
                return next == DELIVERED;
            default:
                return false;
        }
    }
}
