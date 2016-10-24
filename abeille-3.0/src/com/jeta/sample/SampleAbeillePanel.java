/**
 * 
 */
package com.jeta.sample;

import com.jeta.swingbuilder.gui.main.MainPanel;
import com.jeta.swingbuilder.gui.main.MainPanelNames;

/**
 * @author Wolcen
 *
 */
public class SampleAbeillePanel extends MainPanel {

	public SampleAbeillePanel() {
		super();
		/**
		 * Hide some menu
		 */
		this.hideMenuBar(MainPanelNames.ID_ABEILLEFORMS_HELP);
		
		this.hideToolBar(MainPanelNames.ID_SHOW_GRID);
		
	}

}
