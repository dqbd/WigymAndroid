package cz.duong.wigym.ui.adapters.suplovani;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayView;

import cz.duong.wigym.R;
import cz.duong.wigym.data.suplovani.CellData;
import cz.duong.wigym.data.suplovani.ChangeData;
import cz.duong.wigym.data.suplovani.ClassData;
import cz.duong.wigym.data.suplovani.DayInfoData;
import cz.duong.wigym.data.suplovani.RoomData;
import cz.duong.wigym.data.suplovani.TeacherData;
import io.realm.RealmList;

public class SuplovaniAdapter extends BaseExpandableListAdapter {

    private class LabelHolder {
        TextView basic_name;
        TextView detail_name;
    }

    private class ContentHolder {
        TextView detail;
        TextView times;
        TextView lesson;
        TextView room;
        TextView group;

        TwoWayView table;
    }

    private Context mContext;
    private DayInfoData mData;

    private int GROUP_LABEL_TEACHER = 1;
    private int GROUP_LABEL_ROOM = 2;
    private int GROUP_LABEL_CLASS = 3;

    private SparseIntArray groupLabelPosition = new SparseIntArray();
    private SparseArray<SuplovaniTableAdapter> tableAdapters = new SparseArray<SuplovaniTableAdapter>();

    private int groupCount = 0;

    public SuplovaniAdapter(Context context, DayInfoData realmResults) {
        mContext = context;
        mData = realmResults;

        if(mData != null) {
            groupCount = (mData.getTeachers().size() > 0 ? 1 + mData.getTeachers().size() : 0) +
                    (mData.getRooms().size() > 0 ? 1 + mData.getRooms().size() : 0) +
                    (mData.getClasses().size() > 0 ? 1 + mData.getClasses().size() : 0);


            groupLabelPosition.put(0, GROUP_LABEL_TEACHER);
            groupLabelPosition.put(
                    ((mData.getTeachers().size() > 0) ? (mData.getTeachers().size() + 1) : 0),
                    GROUP_LABEL_ROOM
            );
            groupLabelPosition.put(
                    ((mData.getTeachers().size() > 0) ? (mData.getTeachers().size() + 1) : 0) +
                            ((mData.getRooms().size() > 0) ? (mData.getRooms().size() + 1) : 0),
                    GROUP_LABEL_CLASS
            );

        }
    }

    @Override
    public int getGroupCount() {
        return groupCount;
    }

    @Override
    public int getChildrenCount(int groupPos) {
        int groupValue = getGroupType(groupPos);

        Object group = getGroup(groupPos);

        if (groupValue == GROUP_LABEL_CLASS && group != null) {
            return ((ClassData) group).getChanges().size();
        } else if (groupValue > 0) {
            return 1;
        }

        return 0;
    }

    @Override
    public int getChildTypeCount() {
        return 4;
    }

    @Override
    public int getGroupTypeCount() {
        return 4;
    }

    @Override
    public int getGroupType(int groupPosition) {
        if(groupLabelPosition.indexOfKey(groupPosition) >= 0) {
            return -groupLabelPosition.get(groupPosition);
        }

        int valueType = groupLabelPosition.valueAt(0);
        for(int i = 0; i < groupLabelPosition.size(); i++) {
            if(groupPosition > groupLabelPosition.keyAt(i) && groupLabelPosition.valueAt(i) > valueType) {
                valueType = groupLabelPosition.valueAt(i);
            }
        }

        return valueType;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return getGroupType(groupPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {

        if(groupLabelPosition.indexOfKey(groupPosition) < 0)  {
            int groupType = getGroupType(groupPosition);

            for(int i = 0; i < groupLabelPosition.size(); i++) {
                if(groupLabelPosition.valueAt(i) == groupType) {
                    int position = groupPosition - groupLabelPosition.keyAt(i) - 1;

                    if(groupType == GROUP_LABEL_TEACHER) {
                        return mData.getTeachers().get(position);
                    } else if (groupType == GROUP_LABEL_ROOM) {
                        return mData.getRooms().get(position);

                    } else if (groupType == GROUP_LABEL_CLASS) {
                        return mData.getClasses().get(position);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Object getChild(int i, int i2) {
        Object group = getGroup(i);
        int groupType = getGroupType(i);

        if(groupType == GROUP_LABEL_TEACHER) {
            return ((TeacherData) group).getLessons();
        } else if (groupType == GROUP_LABEL_ROOM) {
            return ((RoomData) group).getLessons();

        } else if (groupType == GROUP_LABEL_CLASS) {
            return ((ClassData) group).getChanges().get(i2);
        }

        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i2) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        int groupType = getGroupType(i);
        LabelHolder holder;

        if(convertView == null) {
            holder = new LabelHolder();

            if(groupType < 0) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.suplovani_label, viewGroup, false);
            } else if (groupType == GROUP_LABEL_TEACHER) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.suplovani_teacher, viewGroup, false);
            } else if (groupType == GROUP_LABEL_ROOM) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.suplovani_teacher, viewGroup, false);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.suplovani_class, viewGroup, false);
                holder.detail_name = (TextView) convertView.findViewById(R.id.suplovani_detailtext);
            }

            holder.basic_name = (TextView) convertView.findViewById(R.id.suplovani_maintext);


            convertView.setTag(holder);
        } else {
            holder = (LabelHolder) convertView.getTag();
        }

        if(groupType < 0) {
            if(groupType == -GROUP_LABEL_TEACHER) {
                holder.basic_name.setText(R.string.suplovani_label_teacher);
            } else if (groupType == -GROUP_LABEL_CLASS) {
                holder.basic_name.setText(R.string.suplovani_label_class);
            } else if (groupType == -GROUP_LABEL_ROOM) {
                holder.basic_name.setText(R.string.suplovani_label_room);
            }
        } else {
            Object group = getGroup(i);

            if(groupType == GROUP_LABEL_TEACHER) {
                holder.basic_name.setText(((TeacherData) group).getName());
            } else if (groupType == GROUP_LABEL_ROOM) {
                holder.basic_name.setText(((RoomData) group).getName());
            } else if (groupType == GROUP_LABEL_CLASS) {
                ClassData _class = (ClassData) group;
                holder.basic_name.setText(_class.getName());
                holder.detail_name.setText(
                    mContext.getResources().getQuantityString(
                        R.plurals.suplovani_changes,
                        _class.getChanges().size(),
                        _class.getChanges().size()
                    )
                );
            }
        }

        return convertView;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View convertView, ViewGroup viewGroup) {
        ContentHolder holder;
        int groupType = getGroupType(i);

        if(convertView == null) {
            holder = new ContentHolder();

            if(groupType == GROUP_LABEL_CLASS) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.suplovani_class_change, viewGroup, false);
                holder.detail = (TextView) convertView.findViewById(R.id.suplovani_change_detail);
                holder.group = (TextView) convertView.findViewById(R.id.suplovani_change_group);
                holder.lesson = (TextView) convertView.findViewById(R.id.suplovani_change_lesson);
                holder.room = (TextView) convertView.findViewById(R.id.suplovani_change_room);
                holder.times = (TextView) convertView.findViewById(R.id.suplovani_change_times);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.suplovani_table, viewGroup, false);
                holder.table = (TwoWayView) convertView.findViewById(R.id.suplovani_table);
            }

            convertView.setTag(holder);
        } else {
            holder = (ContentHolder) convertView.getTag();
        }

        if(groupType == GROUP_LABEL_CLASS) {
            ChangeData data = (ChangeData) getChild(i, i2);

            if(!data.isCondensed()) {
                holder.group.setText(data.getGroup());
                holder.lesson.setText(data.getLesson());
                holder.room.setText(data.getRoom());
                holder.times.setText(data.getTime());

                holder.group.setVisibility(View.VISIBLE);
                holder.lesson.setVisibility(View.VISIBLE);
                holder.room.setVisibility(View.VISIBLE);
                holder.times.setVisibility(View.VISIBLE);

            } else {
                holder.group.setVisibility(View.GONE);
                holder.lesson.setVisibility(View.GONE);
                holder.room.setVisibility(View.GONE);
                holder.times.setVisibility(View.GONE);
            }

            String detail = (data.getChange() + " " + data.getDesc() + " " +data.getDetail());
            holder.detail.setText(detail.replaceAll("\\s{2,}", " "));
        } else {
            @SuppressWarnings("unchecked")
            RealmList<CellData> cells = (RealmList<CellData>) getChild(i, i2);
            if(cells != null) {
                holder.table.setAdapter(tableAdapters.get(cells.hashCode(), new SuplovaniTableAdapter(mContext, cells, mData.getHeaders())));
            }
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }
}
