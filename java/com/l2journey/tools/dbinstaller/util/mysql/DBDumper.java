/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2journey.tools.dbinstaller.util.mysql;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.l2journey.tools.dbinstaller.DBOutputInterface;
import com.l2journey.tools.dbinstaller.util.FileWriterStdout;

/**
 * @author mrTJO
 */
public class DBDumper
{
	DBOutputInterface _frame;
	String _db;

	private static Statement createTablesStatement(Connection con)
	{
		try
		{
			return con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		}
		catch (SQLException e)
		{
			try
			{
				return con.createStatement();
			}
			catch (SQLException e2)
			{
				throw new RuntimeException(e2);
			}
		}
	}
	
	public DBDumper(DBOutputInterface frame, String db, String dir)
	{
		_frame = frame;
		_db = db;
		createDump(dir);
	}
	
	public void createDump(String dir)
	{
		try (Formatter form = new Formatter())
		{
			final Connection con = _frame.getConnection();
			try (Statement s = createTablesStatement(con);
				ResultSet rset = s.executeQuery("SHOW TABLES"))
			{
				final File dumpsDir = new File(dir + "../../dumps");
				dumpsDir.mkdirs();
				final File dump = new File(dumpsDir, form.format("%1$s_dump_%2$tY%2$tm%2$td-%2$tH%2$tM%2$tS.sql", _db, new GregorianCalendar().getTime()).toString());
				dump.createNewFile();
				
				_frame.appendToProgressArea("Writing dump " + dump.getName());
				try
				{
					if (rset.last())
					{
						final int rows = rset.getRow();
						rset.beforeFirst();
						if (rows > 0)
						{
							_frame.setProgressIndeterminate(false);
							_frame.setProgressMaximum(rows);
						}
					}
				}
				catch (SQLException e)
				{
					// Ignore: MySQL JDBC may return TYPE_FORWARD_ONLY ResultSet; row counting is optional.
				}
				
				try (FileWriter fileWriter = new FileWriter(dump);
					FileWriterStdout fws = new FileWriterStdout(fileWriter))
				{
					while (rset.next())
					{
						_frame.setProgressValue(rset.getRow());
						_frame.appendToProgressArea("Dumping Table " + rset.getString(1));
						fws.println("CREATE TABLE `" + rset.getString(1) + "`");
						fws.println("(");
						try (Statement desc = con.createStatement();
							ResultSet dset = desc.executeQuery("DESC " + rset.getString(1)))
						{
							final Map<String, List<String>> keys = new HashMap<>();
							boolean isFirst = true;
							while (dset.next())
							{
								if (!isFirst)
								{
									fws.println(",");
								}
								fws.print("\t`" + dset.getString(1) + "`");
								fws.print(" " + dset.getString(2));
								if (dset.getString(3).equals("NO"))
								{
									fws.print(" NOT NULL");
								}
								if (!dset.getString(4).isEmpty())
								{
									if (!keys.containsKey(dset.getString(4)))
									{
										keys.put(dset.getString(4), new ArrayList<>());
									}
									keys.get(dset.getString(4)).add(dset.getString(1));
								}
								if (dset.getString(5) != null)
								{
									fws.print(" DEFAULT '" + dset.getString(5) + "'");
								}
								if (!dset.getString(6).isEmpty())
								{
									fws.print(" " + dset.getString(6));
								}
								isFirst = false;
							}
							if (keys.containsKey("PRI"))
							{
								fws.println(",");
								fws.print("\tPRIMARY KEY (");
								isFirst = true;
								for (String key : keys.get("PRI"))
								{
									if (!isFirst)
									{
										fws.print(", ");
									}
									fws.print("`" + key + "`");
									isFirst = false;
								}
								fws.print(")");
							}
							if (keys.containsKey("MUL"))
							{
								fws.println(",");
								isFirst = true;
								for (String key : keys.get("MUL"))
								{
									if (!isFirst)
									{
										fws.println(", ");
									}
									fws.print("\tKEY `key_" + key + "` (`" + key + "`)");
									isFirst = false;
								}
							}
							fws.println();
							fws.println(");");
							fws.flush();
						}
						
						try (Statement desc = con.createStatement();
							ResultSet dset = desc.executeQuery("SELECT * FROM " + rset.getString(1)))
						{
							boolean isFirst = true;
							int cnt = 0;
							while (dset.next())
							{
								if ((cnt % 100) == 0)
								{
									fws.println("INSERT INTO `" + rset.getString(1) + "` VALUES ");
								}
								else
								{
									fws.println(",");
								}
								
								fws.print("\t(");
								boolean isInFirst = true;
								for (int i = 1; i <= dset.getMetaData().getColumnCount(); i++)
								{
									if (!isInFirst)
									{
										fws.print(", ");
									}
									
									if (dset.getString(i) != null)
									{
										fws.print("'" + dset.getString(i).replace("\'", "\\\'") + "'");
									}
									else
									{
										fws.print("NULL");
									}
									isInFirst = false;
								}
								fws.print(")");
								isFirst = false;
								if ((cnt % 100) == 99)
								{
									fws.println(";");
								}
								cnt++;
							}
							if (!isFirst && ((cnt % 100) != 0))
							{
								fws.println(";");
							}
							fws.println();
							fws.flush();
						}
					}
					fws.flush();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		_frame.appendToProgressArea("Dump Complete!");
	}
}
