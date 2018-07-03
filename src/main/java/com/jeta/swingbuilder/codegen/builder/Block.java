/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.codegen.builder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Block implements CodeSegment {
	public static final String SINGLEQUOTE = "'";
	public static final String DOUBLEQUOTE = "\"";
	
	private LinkedList m_codeLines = new LinkedList();

	public void addCode(String codeLines) {
		m_codeLines.add(codeLines);
	}

	public void println() {
		m_codeLines.add("\n");
	}

	public void output(SourceBuilder builder) {
		Iterator iter = m_codeLines.iterator();
		while (iter.hasNext()) {
			String code = (String) iter.next();
			StringTokenizer st = new StringTokenizer(code, "{};\n\"'\\", true);
			String token0 = "";
			boolean skipln = false;
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				while(token.equals("\n") && skipln){
					if (!st.hasMoreTokens()) break;
					token = st.nextToken();
				}
				skipln = false;
				
				if (!token0.equals("\\") && (token.equals(SINGLEQUOTE) || token.equals(DOUBLEQUOTE))) {
					String tokens = token;
					String token1 = token;
					String token2 = token;
					while (st.hasMoreTokens()) {
						token2 = st.nextToken();
						if (!token1.equals("\\") && (token2.equals(SINGLEQUOTE) || token2.equals(DOUBLEQUOTE))) {
							tokens += token2;
							break;
						}else{
							token1 = token2;
							if(token2.equals("\\"))
								tokens += "\\\\";
							else if(token2.equals("\""))
								tokens += "\\\"";
							else if(token2.equals("\'"))
								tokens += "\\\'";
							else
								tokens += token2;
						}
					}
					builder.print(tokens);
					token = tokens;
				}else if (token.equals("\\")) {
					builder.print("\\\\");
				}else if (token.equals("{")) {
					if(!"\n".equals(token0)){
						builder.println();
					}
					builder.openBrace();
					builder.println();
					skipln = true;
					builder.indent();
				}
				else if (token.equals("}")) {
					builder.dedent();
					builder.closeBrace();
					builder.println();
					skipln = true;
				}
				else if (token.equals(";")) {
					builder.println(";");
				}else {
					if (token.equals("\n")) 
						builder.println();
					else
						builder.print(token);
				}
				token0 = token;
			}
		}
	}
}
