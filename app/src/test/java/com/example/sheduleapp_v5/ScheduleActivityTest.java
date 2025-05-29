package com.example.sheduleapp_v5;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.sheduleapp_v5.models.ScheduleResponse;
import com.example.sheduleapp_v5.network.ScheduleApi;
import com.example.sheduleapp_v5.utils.GroupUtils;
import com.example.sheduleapp_v5.utils.PreferenceManager;
import com.example.sheduleapp_v5.utils.TeacherUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.Shadows;

import java.lang.reflect.Method;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = TestApplication.class)
public class ScheduleActivityTest {

    private ScheduleActivity activity;
    @Mock
    private PreferenceManager preferenceManager;
    @Mock
    private ConnectivityManager connectivityManager;
    @Mock
    private NetworkInfo networkInfo;
    @Mock
    private ScheduleApi scheduleApi;
    @Mock
    private Call<ScheduleResponse> scheduleCall;

    private MockedStatic<GroupUtils> mockedGroupUtils;
    private MockedStatic<TeacherUtils> mockedTeacherUtils;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Закрываем предыдущие моки, если они существуют
        if (mockedGroupUtils != null) {
            mockedGroupUtils.close();
        }
        if (mockedTeacherUtils != null) {
            mockedTeacherUtils.close();
        }

        // Создаем новые моки
        mockedGroupUtils = mockStatic(GroupUtils.class);
        mockedTeacherUtils = mockStatic(TeacherUtils.class);

        // Для void методов используем doNothing()
        mockedGroupUtils.when(() -> GroupUtils.init(any(Context.class))).then(invocation -> null);
        mockedTeacherUtils.when(() -> TeacherUtils.init(any(Context.class))).then(invocation -> null);
        mockedGroupUtils.when(() -> GroupUtils.getGroupId("И-322")).thenReturn(123);

        activity = Robolectric.buildActivity(ScheduleActivity.class)
                .create(new Bundle())
                .start()
                .resume()
                .get();

        // Настройка системного сервиса через Robolectric
        ShadowApplication shadowApplication = Shadows.shadowOf(activity.getApplication());
        shadowApplication.setSystemService(Context.CONNECTIVITY_SERVICE, connectivityManager);

        // Настройка моков
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.isConnected()).thenReturn(true);
        when(preferenceManager.getDefaultGroup()).thenReturn("И-322");
        when(preferenceManager.getScheduleCache()).thenReturn(null);
        when(preferenceManager.getCachedGroupId()).thenReturn(732);
    }

    @After
    public void tearDown() {
        if (mockedGroupUtils != null) {
            mockedGroupUtils.close();
            mockedGroupUtils = null;
        }
        if (mockedTeacherUtils != null) {
            mockedTeacherUtils.close();
            mockedTeacherUtils = null;
        }
    }

    @Test
    public void testOnCreate_loadsDefaultGroup() {
        AutoCompleteTextView etSearch = activity.findViewById(R.id.etSearch);
        assertEquals("", etSearch.getText().toString());
    }

    @Test
    public void testIsNetworkAvailable_true() throws Exception {
        Method isNetworkAvailable = ScheduleActivity.class.getDeclaredMethod("isNetworkAvailable");
        isNetworkAvailable.setAccessible(true);
        assertTrue((Boolean) isNetworkAvailable.invoke(activity));
    }

    @Test
    public void testLoadCachedSchedule_noCache() throws Exception {
        when(preferenceManager.getScheduleCache()).thenReturn(null);
        Method loadCachedSchedule = ScheduleActivity.class.getDeclaredMethod("loadCachedSchedule", int.class);
        loadCachedSchedule.setAccessible(true);
        loadCachedSchedule.invoke(activity, 732);
        assertEquals("Нет кэшированных данных для этой группы", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testFormatTeacherName() throws Exception {
        Method formatTeacherName = ScheduleActivity.class.getDeclaredMethod("formatTeacherName", String.class);
        formatTeacherName.setAccessible(true);
        String result = (String) formatTeacherName.invoke(activity, "Иванов Иван Иванович");
        assertEquals("Иванов И.И.", result);
    }

    @Test
    public void testFetchSchedule_success() {
        ScheduleResponse response = new ScheduleResponse();
        response.setCurrentWeekType(1);
        response.setCurrentWeekName("01.01.2025-07.01.2025");
        response.setItems(new ArrayList<>());

        when(scheduleApi.getSchedule(anyInt())).thenReturn(scheduleCall);
        doAnswer(invocation -> {
            Callback<ScheduleResponse> callback = invocation.getArgument(0);
            callback.onResponse(scheduleCall, Response.success(response));
            return null;
        }).when(scheduleCall).enqueue(any(Callback.class));

        activity.fetchSchedule(732);

        TextView tvWeekType = activity.findViewById(R.id.tvWeekType);
        TextView tvWeekRange = activity.findViewById(R.id.tvWeekRange);
        assertEquals("Тип недели:", tvWeekType.getText().toString());
        assertEquals("[01 мес–07 мес]", tvWeekRange.getText().toString());
    }
}