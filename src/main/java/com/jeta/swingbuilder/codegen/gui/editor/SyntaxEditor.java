/**
 * 
 */ 
package com.jeta.swingbuilder.codegen.gui.editor;

import java.awt.BorderLayout;
import java.io.StringReader;

import javax.swing.JPanel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.jeta.open.gui.framework.JETAPanel;

/**
 * simple introduction
 *
 * <p>detailed comment
 * @author foxhis 2013-8-13
 * @see
 * @since 1.0
 */
public class SyntaxEditor extends JETAPanel {
	private RTextScrollPane scrollPane;
	private RSyntaxTextArea textArea;
	private JPanel m_editor;


	public SyntaxEditor() {
		setLayout(new BorderLayout());

		add(buildView(), BorderLayout.CENTER);
	}

	private JPanel buildView() {

		textArea = new RSyntaxTextArea(25, 70);
		textArea.setTabSize(3);
		textArea.setCaretPosition(0);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		textArea.setCodeFoldingEnabled(true);
		textArea.setClearWhitespaceLinesEnabled(false);
		//textArea.setWhitespaceVisible(true);
		textArea.setPaintMatchedBracketPair(true);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		
		scrollPane = new RTextScrollPane(textArea, true);
		
		m_editor = new JPanel(new BorderLayout());
		m_editor.add(scrollPane, BorderLayout.CENTER);
		
		return m_editor;
	}
	
	public RSyntaxTextArea getTextArea(){
		return textArea;
	}
	
	public void setSyntaxStyle(String style){
		textArea.setSyntaxEditingStyle(style);
	}

	public void setEditable(boolean enabled){
		textArea.setEditable(enabled);
	}

	public String getText() {
		return textArea.getText();
	}

	public void setText(String txt) {
		try {
			StringReader r = new StringReader(txt);
			textArea.read(r, null);
			r.close();
			textArea.setCaretPosition(0);
			textArea.discardAllEdits();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
