package com.example.sheduleapp_v5.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sheduleapp_v5.R;
import com.example.sheduleapp_v5.db.NoteRepository;
import com.example.sheduleapp_v5.models.DisplayLessonItem;
import com.example.sheduleapp_v5.models.LessonItem;
import com.example.sheduleapp_v5.utils.LessonDiffUtil;
import com.example.sheduleapp_v5.work.ReminderScheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DisplayLessonItem> allItems;
    private List<DisplayLessonItem> visibleItems;
    private final NoteRepository noteRepository;

    public LessonAdapter(Context context, List<DisplayLessonItem> lessonList) {
        this.allItems = lessonList;
        this.visibleItems = new ArrayList<>();
        this.noteRepository = new NoteRepository(context);

        for (DisplayLessonItem item : lessonList) {
            if (item.getType() == DisplayLessonItem.TYPE_HEADER || item.isVisible()) {
                if (item.getType() == DisplayLessonItem.TYPE_LESSON) {
                    String key = getLessonKey(item);
                    item.setNote(noteRepository.loadNote(key));
                }
                visibleItems.add(item);
            }
        }
    }

    @Override
    public int getItemCount() {
        return visibleItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return visibleItems.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == DisplayLessonItem.TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day_header, parent, false);
            Log.d("LessonAdapter", "Inflated item_day_header");
            return new HeaderViewHolder(view);
        } else {
            try {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
                Log.d("LessonAdapter", "Inflated item_lesson successfully");
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                if (params != null) {
                    params.bottomMargin = (int) (parent.getContext().getResources().getDisplayMetrics().density * 8); // 8dp
                    view.setLayoutParams(params);
                }
                return new LessonViewHolder(view);
            } catch (Exception e) {
                Log.e("LessonAdapter", "Failed to inflate item_lesson", e);
                throw e;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            DisplayLessonItem item = visibleItems.get(position);

            if (holder instanceof HeaderViewHolder) {
                HeaderViewHolder h = (HeaderViewHolder) holder;
                h.tvDayHeader.setText(item.getDayOfWeek());

                boolean isExpanded = isDayExpanded(item.getDayId());
                h.ivArrow.animate().rotation(isExpanded ? 180f : 0f).setDuration(200).start();

                holder.itemView.setOnClickListener(v -> {
                    toggleDayVisibility(item.getDayId());
                    notifyDataSetChanged();
                });

            } else if (holder instanceof LessonViewHolder) {
                LessonViewHolder lessonHolder = (LessonViewHolder) holder;

                String startTime = item.getStartTime() != null ? item.getStartTime() : "—";
                String endTime = item.getEndTime() != null ? item.getEndTime() : "—";
                Log.d("LessonAdapter", "Binding lesson at position: " + position + ", Time: " + startTime + " - " + endTime);

                lessonHolder.tvTime.setText(startTime + " - " + endTime);

                StringBuilder builder = new StringBuilder();
                if (item.getLessons() != null) {
                    for (int i = 0; i < item.getLessons().size(); i++) {
                        LessonItem lesson = item.getLessons().get(i);
                        builder.append("Предмет: ").append(lesson.getLessonName() != null ? lesson.getLessonName() : "—").append("\n")
                                .append("Преподаватель: ").append(lesson.getTeacherName() != null ? lesson.getTeacherName() : "—").append("\n")
                                .append("Аудитория: ").append(lesson.getClassroom() != null ? lesson.getClassroom() : "—")
                                .append(lesson.getLocation() != null ? " (" + lesson.getLocation() + ")" : "").append("");

                        boolean hasExtras = lesson.getComment() != null || lesson.getSubgroup() != null || lesson.getWeekType() != null;
                        if (hasExtras) {
                            builder.append("\n");

                            if (lesson.getWeekType() != null && lesson.getWeekType().equals(item.getCurrentWeekType())) {
                                builder.append(lesson.getWeekType() == 1 ? "🟢 " : "🔺 ");
                            }

                            builder.append("⚙️ ");

                            if (lesson.getSubgroup() != null) builder.append("подгр. ").append(lesson.getSubgroup()).append(" ");
                            if (lesson.getComment() != null) builder.append(lesson.getComment()).append(" ");
                        }

                        // Добавляем разделяющую линию, если это не последний элемент
                        if (i < item.getLessons().size() - 1) {
                            builder.append("\n\n-----\n\n");
                        } else {
                            builder.append("\n\n");
                        }
                    }
                } else {
                    builder.append("Нет данных");
                }

                // Создаём SpannableString для выделения "Предмет" и "Аудитория" жирным
                SpannableString spannable = new SpannableString(builder.toString().trim());
                String text = spannable.toString();
                int startIndex = 0;
                while ((startIndex = text.indexOf("Предмет:", startIndex)) != -1) {
                    spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startIndex, startIndex + 8, 0);
                    startIndex += 8;
                }
                while ((startIndex = text.indexOf("Аудитория:", startIndex)) != -1) {
                    spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startIndex, startIndex + 10, 0);
                    startIndex += 10;
                }

                Log.d("LessonAdapter", "Details: " + spannable.toString());
                lessonHolder.tvDetails.setText(spannable);

                int currentWeekType = item.getCurrentWeekType();
                boolean hasWeek1 = false, hasWeek2 = false;

                if (item.getLessons() != null) {
                    for (LessonItem lesson : item.getLessons()) {
                        if (lesson.getWeekType() != null) {
                            if (lesson.getWeekType() == 1) hasWeek1 = true;
                            if (lesson.getWeekType() == 2) hasWeek2 = true;
                        }
                    }
                }

                lessonHolder.iconCircle.setVisibility(hasWeek1 ? View.VISIBLE : View.GONE);
                if (hasWeek1) {
                    lessonHolder.iconCircle.setImageResource(currentWeekType == 1 ? R.drawable.ic_circle_filled : R.drawable.ic_circle_outline);
                }

                lessonHolder.iconTriangle.setVisibility(hasWeek2 ? View.VISIBLE : View.GONE);
                if (hasWeek2) {
                    lessonHolder.iconTriangle.setImageResource(currentWeekType == 2 ? R.drawable.ic_triangle_filled : R.drawable.ic_triangle_outline);
                }

                if (item.getNote() != null && !item.getNote().trim().isEmpty()) {
                    lessonHolder.ivNoteIcon.setVisibility(View.VISIBLE);
                    lessonHolder.ivNoteIcon.setOnClickListener(v -> {
                        new AlertDialog.Builder(v.getContext())
                                .setTitle("Заметка")
                                .setMessage(item.getNote())
                                .setPositiveButton("ОК", null)
                                .show();
                    });
                } else {
                    lessonHolder.ivNoteIcon.setVisibility(View.GONE);
                }

                lessonHolder.itemView.setOnLongClickListener(v -> {
                    Context context = v.getContext();
                    AlertDialog.Builder builderDialog = new AlertDialog.Builder(context);
                    builderDialog.setTitle("Добавить заметку");

                    View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_note_input, null);
                    EditText etNote = dialogView.findViewById(R.id.etNote);
                    Button btnPickDate = dialogView.findViewById(R.id.btnPickDate);
                    TextView tvSelectedDate = dialogView.findViewById(R.id.tvSelectedDate);
                    Button btnPickTime = dialogView.findViewById(R.id.btnPickTime);
                    TextView tvSelectedTime = dialogView.findViewById(R.id.tvSelectedTime);

                    etNote.setText(item.getNote());

                    final Calendar calendar = Calendar.getInstance();
                    final long[] remindAt = {0};

                    btnPickDate.setOnClickListener(view -> {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (datePicker, year, month, dayOfMonth) -> {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            tvSelectedDate.setText(String.format("Выбрано: %02d.%02d.%d", dayOfMonth, month + 1, year));
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                    });

                    btnPickTime.setOnClickListener(view -> {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (timePicker, hourOfDay, minute) -> {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 0);
                            remindAt[0] = calendar.getTimeInMillis();
                            tvSelectedTime.setText(String.format("Выбрано: %02d:%02d", hourOfDay, minute));
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                        timePickerDialog.show();
                    });

                    builderDialog.setView(dialogView);

                    builderDialog.setPositiveButton("Сохранить", ((dialog, which) -> {
                        String note = etNote.getText().toString().trim();
                        item.setNote(note);

                        noteRepository.saveNote(getLessonKey(item), note, remindAt[0]);

                        if (remindAt[0] > 0) {
                            String lessonName = "Занятие";
                            if (!item.getLessons().isEmpty()) {
                                LessonItem firstLesson = item.getLessons().get(0);
                                lessonName = firstLesson.getLessonName() != null ? firstLesson.getLessonName() : "—";
                            }
                            String lessonTime = item.getStartTime() + " - " + item.getEndTime();

                            ReminderScheduler.scheduleReminder(context,
                                    getLessonKey(item),
                                    note,
                                    lessonName,
                                    lessonTime,
                                    remindAt[0]);
                        }

                        notifyItemChanged(holder.getAdapterPosition());
                    }));

                    builderDialog.setNegativeButton("Отмена", ((dialog, which) -> dialog.cancel()));

                    builderDialog.show();
                    return true;
                });
            }
        } catch (Exception e) {
            Log.e("LessonAdapter", "Error in onBindViewHolder at position " + position, e);
            throw new RuntimeException("Failed to bind view holder", e);
        }
    }

    private void toggleDayVisibility(String dayId) {
        boolean shouldExpand = false;

        for (DisplayLessonItem item : allItems) {
            if (item.getType() == DisplayLessonItem.TYPE_LESSON && item.getDayId().equals(dayId)) {
                if (!item.isVisible()) {
                    shouldExpand = true;
                    break;
                }
            }
        }

        for (DisplayLessonItem item : allItems) {
            if (item.getType() == DisplayLessonItem.TYPE_LESSON && item.getDayId().equals(dayId)) {
                item.setVisible(shouldExpand);
            }
        }

        List<DisplayLessonItem> oldItems = new ArrayList<>(visibleItems);
        visibleItems.clear();
        for (DisplayLessonItem item : allItems) {
            if (item.getType() == DisplayLessonItem.TYPE_HEADER || item.isVisible()) {
                visibleItems.add(item);
            }
        }

        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new LessonDiffUtil(oldItems, visibleItems));
        result.dispatchUpdatesTo(this);
    }

    private boolean isDayExpanded(String dayId) {
        for (DisplayLessonItem item : allItems) {
            if (item.getType() == DisplayLessonItem.TYPE_LESSON && item.getDayId().equals(dayId)) {
                return item.isVisible();
            }
        }
        return false;
    }

    private String getLessonKey(DisplayLessonItem item) {
        return item.getDayId() + "_" + item.getStartTime() + "_" + item.getEndTime();
    }

    public List<DisplayLessonItem> getLessonList() {
        return allItems;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayHeader;
        ImageView ivArrow;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayHeader = itemView.findViewById(R.id.tvDayHeader);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvDetails;
        ImageView iconCircle, iconTriangle, ivNoteIcon;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            iconCircle = itemView.findViewById(R.id.iconCircle);
            iconTriangle = itemView.findViewById(R.id.iconTriangle);
            ivNoteIcon = itemView.findViewById(R.id.ivNoteIcon);
        }
    }
}