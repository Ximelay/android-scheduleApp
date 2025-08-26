package com.example.irkpo_management.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.irkpo_management.R;
import com.example.irkpo_management.db.NoteRepository;
import com.example.irkpo_management.models.DisplayLessonItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE) // Убираем предупреждение об отсутствии AndroidManifest.xml
public class LessonAdapterTest {

    @Mock
    private NoteRepository noteRepository;
    @Mock
    private ViewGroup parent;
    @Mock
    private LayoutInflater layoutInflater;
    @Mock
    private View headerView;
    @Mock
    private View lessonView;
    @Mock
    private Context context; // Мок для Context

    private LessonAdapter adapter;
    private List<DisplayLessonItem> lessonList;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        lessonList = new ArrayList<>();

        // Подготовка тестовых данных
        DisplayLessonItem headerItem = new DisplayLessonItem(
                DisplayLessonItem.TYPE_HEADER, "Понедельник", null, null, null, false, 1);
        DisplayLessonItem lessonItem = new DisplayLessonItem(
                DisplayLessonItem.TYPE_LESSON, "Понедельник", "08:00", "09:30",
                new ArrayList<>(), false, 1);
        lessonList.add(headerItem);
        lessonList.add(lessonItem);

        // Настройка моков
        when(parent.getContext()).thenReturn(context);
        when(layoutInflater.inflate(R.layout.item_day_header, parent, false)).thenReturn(headerView);
        when(layoutInflater.inflate(R.layout.item_lesson, parent, false)).thenReturn(lessonView);
        when(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(layoutInflater);
        when(noteRepository.loadNote(anyString())).thenReturn("Тестовая заметка");

        // Передаем замоканный noteRepository в LessonAdapter
        adapter = new LessonAdapter(context, lessonList, false, noteRepository);
    }

    @Test
    public void testGetItemCount() {
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testGetItemViewType_header() {
        assertEquals(DisplayLessonItem.TYPE_HEADER, adapter.getItemViewType(0));
    }

    @Test
    public void testGetItemViewType_lesson() {
        assertEquals(DisplayLessonItem.TYPE_LESSON, adapter.getItemViewType(1));
    }

    @Test
    public void testOnCreateViewHolder_header() {
        RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(parent, DisplayLessonItem.TYPE_HEADER);
        assertTrue(holder instanceof LessonAdapter.HeaderViewHolder);
    }

    @Test
    public void testOnCreateViewHolder_lesson() {
        RecyclerView.ViewHolder holder = adapter.onCreateViewHolder(parent, DisplayLessonItem.TYPE_LESSON);
        assertTrue(holder instanceof LessonAdapter.LessonViewHolder);
    }

    @Test
    public void testToggleDayVisibility() {
        adapter.toggleDayVisibility("Понедельник");
        assertEquals(1, adapter.getItemCount()); // Только заголовок виден
        adapter.toggleDayVisibility("Понедельник");
        assertEquals(2, adapter.getItemCount()); // Заголовок и урок видны
    }

    @Test
    public void testGetLessonKey() throws Exception {
        DisplayLessonItem item = lessonList.get(1);
        Method getLessonKey = LessonAdapter.class.getDeclaredMethod("getLessonKey", DisplayLessonItem.class);
        getLessonKey.setAccessible(true);
        String key = (String) getLessonKey.invoke(adapter, item);
        assertEquals("Понедельник_08:00_09:30", key);
    }
}