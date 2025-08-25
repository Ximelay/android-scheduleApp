package com.example.stud_informer_irkpo.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.stud_informer_irkpo.models.DisplayLessonItem;

import java.util.List;

public class LessonDiffUtil extends DiffUtil.Callback {
    private final List<DisplayLessonItem> oldList;
    private final List<DisplayLessonItem> newList;

    public LessonDiffUtil(List<DisplayLessonItem> oldList, List<DisplayLessonItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        DisplayLessonItem oldItem = oldList.get(oldItemPosition);
        DisplayLessonItem newItem = newList.get(newItemPosition);
        return oldItem.getType() == newItem.getType()
                && oldItem.getDayId().equals(newItem.getDayId())
                && oldItem.getStartTime() != null && oldItem.getStartTime().equals(newItem.getStartTime());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return areItemsTheSame(oldItemPosition, newItemPosition); // упрощённо
    }
}
