package org.jca;

import java.awt.Color;

/**
 * 
 * @author ksdj (coder-hat)
 */
public interface IGridColorProvider {
    
    public Color getCellColor(int iCell);
    
    public Color getBackgroundColor();
}
