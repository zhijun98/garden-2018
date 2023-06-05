/*
 * Copyright 2018 ZComApproach Inc.
 *
 * Licensed under multiple open source licenses involved in the project (the "Licenses");
 * you may not use this file except in compliance with the Licenses.
 * You may obtain copies of the Licenses at
 *
 *      http://www.zcomapproach.com/licenses
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zcomapproach.garden.peony.view.controllers;

import com.zcomapproach.garden.peony.view.PeonyFaceController;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import jfxtras.icalendarfx.VCalendar;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.icalendar.ICalendarAgenda;

/**
 *
 * @author zhijun98
 */
public class PeonySchedulerController extends PeonyFaceController{

    @FXML
    private BorderPane agendaBorderPane;
    @FXML
    private HBox agendaHBox;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        final Agenda agenda = new Agenda();
//        agenda.setSkin(new AgendaDaysFromDisplayedSkin(agenda));
//        agenda.setAllowDragging(false);
//        agenda.setAllowResize(false);
//        // setup appointment groups
//        final Map<String, Agenda.AppointmentGroup> appointmentGroupMap = new TreeMap<>();
//        for (Agenda.AppointmentGroup appointmentGroup : agenda.appointmentGroups()) {
//        	appointmentGroupMap.put(appointmentGroup.getDescription(), appointmentGroup);
//        }
//        // add the skin switcher
//        AgendaSkinSwitcher skinSwitcher = new AgendaSkinSwitcher(agenda);
//        agendaHBox.getChildren().add(skinSwitcher);
//        // add the displayed date textfield
//        LocalDateTimeTextField aLocalDateTimeTextField = new LocalDateTimeTextField();
//        aLocalDateTimeTextField.localDateTimeProperty().bindBidirectional(agenda.displayedLocalDateTime());		
        
        VCalendar vCalendar = new VCalendar();
        	
        ICalendarAgenda agenda = new ICalendarAgenda(vCalendar);
        
        // setup appointment groups
        final Map<String, Agenda.AppointmentGroup> appointmentGroupMap = new TreeMap<String, Agenda.AppointmentGroup>();
        for (Agenda.AppointmentGroup appointmentGroup : agenda.appointmentGroups()) {
        	appointmentGroupMap.put(appointmentGroup.getDescription(), appointmentGroup);
        }
        // initial set
        LocalDate todayLocalDate = LocalDate.now();
        LocalDate tomorrowLocalDate = LocalDate.now().plusDays(1);
        int idx = 0;
        final String lIpsum = "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante. Donec eu libero sit amet quam egestas semper. Aenean ultricies mi vitae est. Mauris placerat eleifend leo. Quisque sit amet est et sapien ullamcorper pharetra. Vestibulum erat wisi, condimentum sed, commodo vitae, ornare sit amet, wisi. Aenean fermentum, elit eget tincidunt condimentum, eros ipsum rutrum orci, sagittis tempus lacus enim ac dui. Donec non enim in turpis pulvinar facilisis. Ut felis. Praesent dapibus, neque id cursus faucibus, tortor neque egestas augue, eu vulputate magna eros eu erat. Aliquam erat volutpat. Nam dui mi, tincidunt quis, accumsan porttitor, facilisis luctus, metus";
        LocalDateTime multipleDaySpannerStartDateTime = todayLocalDate.atStartOfDay().plusHours(5);
        multipleDaySpannerStartDateTime = multipleDaySpannerStartDateTime.minusDays(multipleDaySpannerStartDateTime.getDayOfWeek().getValue() > 3 && multipleDaySpannerStartDateTime.getDayOfWeek().getValue() < 7 ? 3 : -1);
        LocalDateTime multipleDaySpannerEndDateTime = multipleDaySpannerStartDateTime.plusDays(2);
	// testAppointments...
        Appointment[] testAppointments = new Appointment[]{
			new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(8, 00)))
				.withEndLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(11, 30)))
				.withSummary("A")
				.withDescription("A much longer test description")
				.withAppointmentGroup(appointmentGroupMap.get("group07"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(8, 30)))
				.withEndLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(10, 00)))
				.withSummary("B")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group08"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(8, 30)))
				.withEndLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(9, 30)))
				.withSummary("C")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group09"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(9, 00)))
				.withEndLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(13, 30)))
				.withSummary("D")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group07"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(10, 30)))
				.withEndLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(11, 00)))
				.withSummary("E")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group07"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(12, 30)))
				.withEndLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(13, 30)))
				.withSummary("F")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group07"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(13, 00)))
				.withEndLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(13, 30)))
				.withSummary("H")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group07"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(14, 00)))
				.withEndLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(14, 45)))
				.withSummary("G")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group07"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(15, 00)))
				.withEndLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(16, 00)))
				.withSummary("I")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group07"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(15, 30)))
				.withEndLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(16, 00)))
				.withSummary("J")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group07"))
		// backwards compatibility: calendar based appointment
		, 	new Agenda.AppointmentImpl()
				.withStartTime(new GregorianCalendar(todayLocalDate.getYear(), todayLocalDate.getMonthValue() - 1, todayLocalDate.getDayOfMonth(), 4, 00))
				.withEndTime(new GregorianCalendar(todayLocalDate.getYear(), todayLocalDate.getMonthValue() - 1, todayLocalDate.getDayOfMonth(), 5, 30))
				.withSummary("Cal")
				.withDescription("Calendar based")
				.withAppointmentGroup(appointmentGroupMap.get("group08"))
		// ZonedDateTime: there is no additional value in using ZonedDateTime everywhere, so we just have one test appointment
		, 	new Agenda.AppointmentImplTemporal()
				.withStartTemporal(ZonedDateTime.of(todayLocalDate, LocalTime.of(2, 00), ZoneId.systemDefault()) )
				.withEndTemporal(ZonedDateTime.of(todayLocalDate, LocalTime.of(3, 30), ZoneId.systemDefault()) )
				.withSummary("Zoned")
				.withDescription("Zoned based")
				.withAppointmentGroup(appointmentGroupMap.get("group08"))
		// -----
		// too short for actual rendering
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(20, 30)))
				.withEndLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(20, 31)))
				.withSummary("S")
				.withDescription("Too short")
				.withAppointmentGroup(appointmentGroupMap.get("group07"))
		// -----
		// tasks
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(8, 10)))
				.withSummary("K kk kkkkk k k k k ")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group17"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(8, 10)))
				.withSummary("M mmm m m m m m mmmm")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group18"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(8, 11)))
				.withSummary("N nnnn n n n  nnnnn")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group19"))
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(LocalDateTime.of(todayLocalDate, LocalTime.of(6, 00)))
				.withSummary("L asfsfd dsfsdfs fsfds sdgsds dsdfsd ")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group11"))
		// -----
		// wholeday
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(todayLocalDate.atStartOfDay())
				.withSummary("whole1")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group17"))
				.withWholeDay(true)
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(todayLocalDate.atStartOfDay())
				.withSummary("whole but then with a long description")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group18"))
				.withWholeDay(true)
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(todayLocalDate.atStartOfDay())
				.withEndLocalDateTime(tomorrowLocalDate.atStartOfDay()) // at we going to do en
				.withSummary("whole3")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group19"))
				.withWholeDay(true)
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(tomorrowLocalDate.atStartOfDay())
				.withEndLocalDateTime(tomorrowLocalDate.atTime(13, 00))
				.withSummary("whole+end")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group13"))
				.withWholeDay(true)
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(todayLocalDate.atStartOfDay())
				.withEndLocalDateTime(tomorrowLocalDate.atTime(13, 00))
				.withSummary("whole+spanning")
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group14"))
				.withWholeDay(true)
		// -----
		// regular spanning
		, 	new Agenda.AppointmentImplLocal()
				.withStartLocalDateTime(multipleDaySpannerStartDateTime)
				.withEndLocalDateTime(multipleDaySpannerEndDateTime)
				.withSummary(lIpsum.substring(0, 20 + new Random().nextInt(lIpsum.length() - 20)))
				.withDescription("A description " + (++idx))
				.withAppointmentGroup(appointmentGroupMap.get("group20"))
		};
        agenda.appointments().addAll(testAppointments);
        agenda.appointments().addListener(new ListChangeListener<Appointment>(){
            @Override
            public void onChanged(ListChangeListener.Change<? extends Appointment> appointment) {
                System.out.println("ListChangeListener -> " + appointment);
            }
        });
        agenda.setActionCallback( (appointment) -> {
                System.out.println("Action on " + appointment);
                return null;
        });
        Button increaseWeekButton = new Button(">");
        increaseWeekButton.setOnAction((ActionEvent event) -> {
            LocalDateTime newDisplayLocalDataTime = agenda.getDisplayedLocalDateTime().plus(Period.ofDays(1));
            agenda.setDisplayedLocalDateTime(newDisplayLocalDataTime);
        });
        Button decreaseWeekButton = new Button("<");
        decreaseWeekButton.setOnAction((ActionEvent event) -> {
            LocalDateTime newDisplayLocalDataTime = agenda.getDisplayedLocalDateTime().minus(Period.ofDays(1));
            agenda.setDisplayedLocalDateTime(newDisplayLocalDataTime);
        });
        
        agendaHBox.getChildren().add(increaseWeekButton);
        agendaHBox.getChildren().add(decreaseWeekButton);
        
        agendaBorderPane.setCenter(agenda);
    }

}
