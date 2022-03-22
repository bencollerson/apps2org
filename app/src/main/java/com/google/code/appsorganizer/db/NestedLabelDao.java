/*
 * Copyright (C) 2009 Apps Organizer
 *
 * This file is part of Apps Organizer
 *
 * Apps Organizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Apps Organizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Apps Organizer.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.google.code.appsorganizer.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.code.appsorganizer.model.NestedLabel;

public class NestedLabelDao extends ObjectWithIdDao<NestedLabel> {

	private static final String OBJECT_LABEL_ID_COL_NAME = "id_object_label";
	private static final String APPLIED_LABEL_ID_COL_NAME = "id_applied_label";

	public static final String TABLE_NAME = "nested_labels";

	private static final String[] COLS_STRING = new String[] { ID_COL_NAME, OBJECT_LABEL_ID_COL_NAME, APPLIED_LABEL_ID_COL_NAME };

	public static final DbColumns OBJECT_LABEL_ID = new DbColumns(OBJECT_LABEL_ID_COL_NAME, "integer not null");
	public static final DbColumns APPLIED_LABEL_ID = new DbColumns(APPLIED_LABEL_ID_COL_NAME, "integer not null");

	private static final DbColumns[] DB_COLUMNS = new DbColumns[] { ID, OBJECT_LABEL_ID, APPLIED_LABEL_ID };

	NestedLabelDao() {
		super(TABLE_NAME);
		columns = DB_COLUMNS;
	}

	public long merge(long objectLableId, long appliedLabelId) {
		Cursor c = db.query(TABLE_NAME, new String[] { ID_COL_NAME }, OBJECT_LABEL_ID_COL_NAME + "= ? and "
				+ APPLIED_LABEL_ID_COL_NAME + "= ?", new String[] { Long.toString(objectLableId), Long.toString(appliedLabelId) }, null, null, null);
		try {
			if (!c.moveToNext()) {
				return insert(objectLableId, appliedLabelId);
			} else {
				return -1;
			}
		} finally {
			c.close();
		}
	}

	public long insert(long objectLableId, long appliedLabelId) {
		ContentValues v = new ContentValues();
		v.put(OBJECT_LABEL_ID_COL_NAME, objectLableId);
		v.put(APPLIED_LABEL_ID_COL_NAME, appliedLabelId);
		return db.insert(name, null, v);
	}

	@Override
	protected NestedLabel createObject(Cursor c) {
		NestedLabel t = new NestedLabel();
		t.setId(c.getLong(0));
		t.setObjectLabelId(c.getLong(1));
		t.setAppliedLabelId(c.getLong(2));
		return t;
	}

	public int delete(Long objectLableId, Long appliedLabelId) {
		return db.delete(name, OBJECT_LABEL_ID_COL_NAME + " = ? and " + APPLIED_LABEL_ID_COL_NAME + " = ?",
				new String[] { objectLableId.toString(), appliedLabelId.toString() });
	}

	public int deleteNestedLabels(Long appliedLabelId) {
		return db.delete(name, APPLIED_LABEL_ID_COL_NAME + " = ?", new String[] { appliedLabelId.toString() });
	}

	@Override
	protected ContentValues createContentValue(NestedLabel obj) {
		ContentValues v = new ContentValues();
		v.put(ID_COL_NAME, obj.getId());
		v.put(OBJECT_LABEL_ID_COL_NAME, obj.getObjectLabelId());
		v.put(APPLIED_LABEL_ID_COL_NAME, obj.getAppliedLabelId());
		return v;
	}

	public static String getCreateTableScript() {
		return getCreateTableScript(TABLE_NAME, DB_COLUMNS);
	}

	public String getLabelListString(Long appliedLabelId) {
		Cursor c = db.rawQuery("select l.label from labels l inner join nested_labels nl "
				+ "on l._id = nl.id_applied_label where nl.id_applied_label = ? order by upper(l.label)", new String[] { appliedLabelId.toString() });
		StringBuilder b = new StringBuilder();
		try {
			while (c.moveToNext()) {
				if (b.length() != 0) {
					b.append(", ");
				}
				b.append(c.getString(0));
			}
		} finally {
			c.close();
		}
		return b.toString();
	}
}
