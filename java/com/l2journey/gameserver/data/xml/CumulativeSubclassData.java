/*
 * Copyright (c) 2025 L2Journey Project
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.l2journey.gameserver.data.xml;

import java.io.File;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2journey.commons.util.IXmlReader;

/**
 * Data handler for Cumulative Subclass (Dual Class) configuration.
 */
public class CumulativeSubclassData implements IXmlReader
{
	private static final Logger LOGGER = Logger.getLogger(CumulativeSubclassData.class.getName());
	
	private boolean _enabled = true;
	private boolean _sameRaceOnly = true;
	private int _requiredLevel = 75;
	private int _requiredItemId = 99000;
	private int _requiredItemCount = 1;
	private int _delevelTargetLevel = 40;
	private String _disabledBehavior = "HIDE";
	
	protected CumulativeSubclassData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDatapackFile("config/custom/CumulativeSubclass.xml");
	}
	
	@Override
	public void parseDocument(Document document, File file)
	{
		for (Node n = document.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equals(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("setting".equals(d.getNodeName()))
					{
						final NamedNodeMap attrs = d.getAttributes();
						final String name = parseString(attrs, "name");
						if (name == null)
						{
							continue;
						}
						
						switch (name)
						{
							case "EnableCumulativeSubclass":
								_enabled = parseBoolean(attrs, "val", true);
								break;
							case "SameRaceOnly":
								_sameRaceOnly = parseBoolean(attrs, "val", true);
								break;
							case "RequiredLevel":
								_requiredLevel = parseInteger(attrs, "val", 75);
								break;
							case "RequiredItemId":
								_requiredItemId = parseInteger(attrs, "val", 99000);
								break;
							case "RequiredItemCount":
								_requiredItemCount = parseInteger(attrs, "val", 1);
								break;
							case "DelevelTargetLevel":
								_delevelTargetLevel = parseInteger(attrs, "val", 40);
								break;
							case "DisabledBehavior":
								_disabledBehavior = parseString(attrs, "val");
								break;
						}
					}
				}
			}
		}
		
		LOGGER.info(getClass().getSimpleName() + ": Loaded settings (Enabled: " + _enabled + ", SameRaceOnly: " + _sameRaceOnly + ", ReqLvl: " + _requiredLevel + ", TargetLvl: " + _delevelTargetLevel + ").");
	}
	
	public boolean isEnabled()
	{
		return _enabled;
	}
	
	public boolean isSameRaceOnly()
	{
		return _sameRaceOnly;
	}
	
	public int getRequiredLevel()
	{
		return _requiredLevel;
	}
	
	public int getRequiredItemId()
	{
		return _requiredItemId;
	}
	
	public int getRequiredItemCount()
	{
		return _requiredItemCount;
	}
	
	public int getDelevelTargetLevel()
	{
		return _delevelTargetLevel;
	}
	
	public String getDisabledBehavior()
	{
		return _disabledBehavior;
	}
	
	public static CumulativeSubclassData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CumulativeSubclassData INSTANCE = new CumulativeSubclassData();
	}
}
