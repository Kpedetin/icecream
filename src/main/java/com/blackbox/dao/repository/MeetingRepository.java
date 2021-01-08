package com.blackbox.dao.repository;

import com.blackbox.dao.enumeration.Day;
import com.blackbox.dao.enumeration.Timeslot;
import com.blackbox.dao.model.Meeting;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MeetingRepository extends MongoRepository<Meeting, String> {

	List<Meeting> findByDayAndTimeslot(Day day, Timeslot timeslot);

	List<Meeting> findByDayAndTimeslotIn(Day day, List<Timeslot> timeslots);

}
