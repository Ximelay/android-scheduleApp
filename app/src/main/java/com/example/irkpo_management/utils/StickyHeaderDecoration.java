package com.example.irkpo_management.utils;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.irkpo_management.R;
import com.example.irkpo_management.adapters.LessonAdapter;
import com.example.irkpo_management.models.DisplayLessonItem;

public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {
    private LessonAdapter adapter;

    public StickyHeaderDecoration(LessonAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildCount() <= 0 || adapter.getItemCount() == 0)
            return;

        // Получаем позицию первого видимого элемента в RecyclerView
        int topChildPosition = ((RecyclerView.LayoutManager) parent.getLayoutManager()).getPosition(parent.getChildAt(0));
        if (topChildPosition == RecyclerView.NO_POSITION)
            return;

        // Получаем список видимых элементов
        DisplayLessonItem currentItem = adapter.getVisibleLessonList().get(topChildPosition);

        // Проверяем, что текущий элемент — это урок
        if (currentItem.type != DisplayLessonItem.TYPE_LESSON)
            return;

        // Ищем ближайший заголовок (TYPE_HEADER) перед текущей позицией
        String currentDay = null;
        for (int i = topChildPosition; i >= 0; i--) {
            DisplayLessonItem item = adapter.getVisibleLessonList().get(i);
            if (item.type == DisplayLessonItem.TYPE_HEADER) {
                currentDay = item.dayOfWeek;
                break;
            }
        }

        // Если заголовок не найден, ничего не отображаем
        if (currentDay == null)
            return;

        // Создаем и настраиваем View для заголовка
        View headerView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_header, parent, false);
        TextView tv = headerView.findViewById(R.id.tvDayHeader);
        tv.setText(currentDay);

        fixLayoutSize(parent, headerView);

        c.save();
        c.translate(0, 0);
        headerView.draw(c);
        c.restore();
    }

    private void fixLayoutSize(ViewGroup parent, View view) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);

        view.measure(childWidth, childHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }
}