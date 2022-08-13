package com.ta3lim.app;

import com.ta3lim.app.domain.Favorite;
import com.ta3lim.app.domain.Message;
import com.ta3lim.app.domain.Notification;
import com.ta3lim.app.domain.Resource;
import com.ta3lim.app.domain.Skill;
import com.ta3lim.app.domain.Subject;
import com.ta3lim.app.domain.Topic;
import com.ta3lim.app.domain.User;
import com.ta3lim.app.domain.UserExtended;
import com.ta3lim.app.domain.Votes;
import com.ta3lim.app.domain.enumeration.AgeRange;
import com.ta3lim.app.domain.enumeration.Children;
import com.ta3lim.app.domain.enumeration.CivilStatus;
import com.ta3lim.app.domain.enumeration.NotificationType;
import com.ta3lim.app.domain.enumeration.ResourceType;
import com.ta3lim.app.repository.FavoriteRepository;
import com.ta3lim.app.repository.MessageRepository;
import com.ta3lim.app.repository.NotificationRepository;
import com.ta3lim.app.repository.ResourceRepository;
import com.ta3lim.app.repository.SkillRepository;
import com.ta3lim.app.repository.SubjectRepository;
import com.ta3lim.app.repository.TopicRepository;
import com.ta3lim.app.repository.UserExtendedRepository;
import com.ta3lim.app.repository.UserRepository;
import com.ta3lim.app.repository.VotesRepository;
import java.time.LocalDate;
import java.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Ta3LimAppDemoDataImporter implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Ta3LimAppDemoDataImporter.class);

    @Autowired
    private ResourceRepository resourcerepo;

    @Autowired
    private UserRepository userrepo;

    @Autowired
    private UserExtendedRepository userextendedrepo;

    @Autowired
    private SubjectRepository subjectrepo;

    @Autowired
    private TopicRepository topicrepo;

    @Autowired
    private NotificationRepository notificationrepo;

    @Autowired
    private MessageRepository messagerepo;

    @Autowired
    private FavoriteRepository favoriterepo;

    @Autowired
    private VotesRepository votesrepo;

    @Autowired
    private SkillRepository skillrepo;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("this ran");
        // fetch all resources
        log.info("Resources found with findAll():");
        log.info("-------------------------------");
        for (Resource resource : resourcerepo.findAll()) {
            log.info(resource.toString());
        }

        //check if admin exists and set profile info
        User admin = userrepo.findOneByLogin("admin@localhost").isPresent() ? userrepo.findOneByLogin("admin@localhost").get() : null;
        UserExtended userextended = userextendedrepo.findById(1L).isPresent() ? userextendedrepo.findById(1L).get() : null;
        if (admin != null && userextended == null) {
            UserExtended u1 = new UserExtended();
            u1.setAboutMe("I'm a homeschooling enthousiast, following the unschooling philosophy");
            u1.setOccupation("IT generalist, Software Developer");
            u1.setSocialMedia("https://www.adambahri.com");
            u1.setCivilStatus(CivilStatus.MARRIED);
            u1.setFirstchild(Children.AGE_04_06);
            u1.setSecondchild(Children.AGE_07_09);
            u1.setFilesquota(200);
            u1.setUser(admin);
            userextendedrepo.save(u1);
        }

        //create first 4 subjects
        Subject subject = subjectrepo.findById(1L).isPresent() ? subjectrepo.findById(1L).get() : null;
        String slabel1 = "Arabic language";
        String slabel2 = "Islamic education";
        String slabel3 = "History";
        String slabel4 = "Mathematics";
        if (subject == null) {
            Subject s1 = new Subject();
            s1.setLabel(slabel1);
            s1.setCreationDate(LocalDate.now());
            subjectrepo.save(s1);
            Subject s2 = new Subject();
            s2.setLabel(slabel2);
            s2.setCreationDate(LocalDate.now());
            subjectrepo.save(s2);
            Subject s3 = new Subject();
            s3.setLabel(slabel3);
            s3.setCreationDate(LocalDate.now());
            subjectrepo.save(s3);
            Subject s4 = new Subject();
            s4.setLabel(slabel4);
            s4.setCreationDate(LocalDate.now());
            subjectrepo.save(s4);
        }

        //create first 4 topics
        Topic topic = topicrepo.findById(1L).isPresent() ? topicrepo.findById(1L).get() : null;
        String tlabel1 = "Reading";
        String tlabel2 = "Writing";
        String tlabel3 = "Arithmetic";
        String tlabel4 = "Morals";
        if (topic == null) {
            Topic t1 = new Topic();
            t1.setLabel(tlabel1);
            t1.setCreationDate(LocalDate.now());
            topicrepo.save(t1);
            Topic t2 = new Topic();
            t2.setLabel(tlabel2);
            t2.setCreationDate(LocalDate.now());
            topicrepo.save(t2);
            Topic t3 = new Topic();
            t3.setLabel(tlabel3);
            t3.setCreationDate(LocalDate.now());
            topicrepo.save(t3);
            Topic t4 = new Topic();
            t4.setLabel(tlabel4);
            t4.setCreationDate(LocalDate.now());
            topicrepo.save(t4);
        }

        //create first 3 skills
        Skill skill = skillrepo.findById(1L).isPresent() ? skillrepo.findById(1L).get() : null;
        String sklabel1 = "Reading";
        String sklabel2 = "Reading comprehension";
        String sklabel3 = "Counting/subtracting";
        if (skill == null) {
            Skill s1 = new Skill();
            s1.setLabel(sklabel1);
            s1.setCreationDate(LocalDate.now());
            skillrepo.save(s1);
            Skill s2 = new Skill();
            s2.setLabel(sklabel2);
            s2.setCreationDate(LocalDate.now());
            skillrepo.save(s2);
            Skill s3 = new Skill();
            s3.setLabel(sklabel3);
            s3.setCreationDate(LocalDate.now());
            skillrepo.save(s3);
        }

        //create first notification if user exists
        Notification notification = notificationrepo.findById(1L).isPresent() ? notificationrepo.findById(1L).get() : null;
        User user = userrepo.findOneByLogin("user").isPresent() ? userrepo.findOneByLogin("user").get() : null;
        if (notification == null && user != null) {
            Notification n1 = new Notification();
            n1.setNotificationText("You have 1 unread message");
            n1.setNotificationType(NotificationType.UNREAD_MESSAGES);
            n1.setUser(user);
            n1.setIsDelivered(true);
            notificationrepo.save(n1);
        }

        //create 2 messages if user & admin exist
        Message message = messagerepo.findById(1L).isPresent() ? messagerepo.findById(1L).get() : null;
        if (message == null && user != null && admin != null) {
            Message m1 = new Message();
            m1.setMessageText("First message");
            m1.setCreationDate(LocalDate.now().minus(Period.ofDays(5)));
            m1.setIsDelivered(true);
            m1.setSender(admin);
            m1.setReceiver(user);
            messagerepo.save(m1);
            Message m2 = new Message();
            m2.setMessageText("I have a question, can I ...");
            m2.setCreationDate(LocalDate.now().minus(Period.ofDays(3)));
            m2.setIsDelivered(true);
            m2.setSender(user);
            m2.setReceiver(admin);
            messagerepo.save(m2);
        }

        //create first 5 resources if user/subject/topic/skill exists in db
        Resource resource = resourcerepo.findById(1L).isPresent() ? resourcerepo.findById(1L).get() : null;
        if (resource == null && user != null && subject != null && topic != null && topic != null) {
            Resource r1 = new Resource();
            r1.setTitle("TuxMath - free game");
            r1.setActivated(true);
            r1.setDescription(
                "TuxMath is an open source, free game whose difficulty is appropriate for students from elementary to high school, in other words, 7 to 13 years."
            );
            r1.setCreationDate(LocalDate.now());
            r1.setUser(user);
            r1.setVotes(202L);
            r1.setAngeRage(AgeRange.AGE_07_09);
            r1.setResourceType(ResourceType.URLS);
            //r1.setSubject(subjectrepo.findByLabel("Mathematics").get());
            r1.addTopics(topicrepo.findByLabel("Arithmetic").get());
            r1.addSkills(skillrepo.findByLabel("Counting/subtracting").get());
            resourcerepo.save(r1);
            Resource r2 = new Resource();
            r2.setTitle("'Iqra' textbooks");
            r2.setActivated(true);
            r2.setDescription(
                "The first series of Arabic textbooks in Morocco. 'Iqra', published by Ahmed Boukmakh. It consists of 5 volumes and these have been made available for download. Highly recommended for learning to read."
            );
            r2.setCreationDate(LocalDate.now());
            r2.setUser(user);
            r2.setVotes(57L);
            r2.setAngeRage(AgeRange.AGE_10_12);
            r2.setResourceType(ResourceType.URLS);
            r2.setSubject(subjectrepo.findByLabel("Arabic language").get());
            r2.addTopics(topicrepo.findByLabel("Reading").get());
            r2.addTopics(topicrepo.findByLabel("Writing").get());
            r2.addSkills(skillrepo.findByLabel("Reading").get());
            resourcerepo.save(r2);
            Resource r3 = new Resource();
            r3.setTitle("Ibn Battuta, Traveler from Tangier");
            r3.setActivated(true);
            r3.setDescription(
                "In the year 1349 a dusty Arab horseman rode slowly toward the city of Tangier on the North African coast. For Ibn Battuta, it was the end of a long journey."
            );
            r3.setCreationDate(LocalDate.now());
            r3.setUser(user);
            r3.setVotes(15L);
            r3.setAngeRage(AgeRange.AGE_10_12);
            r3.setResourceType(ResourceType.DOCUMENTS);
            r3.setSubject(subjectrepo.findByLabel("History").get());
            r3.addTopics(topicrepo.findByLabel("Reading").get());
            r3.addSkills(skillrepo.findByLabel("Reading").get());
            r3.addSkills(skillrepo.findByLabel("Reading comprehension").get());
            resourcerepo.save(r3);
            Resource r4 = new Resource();
            r4.setTitle("Taha TV for Kids");
            r4.setActivated(true);
            r4.setDescription(
                "Taha TV for Kids is available on Youtube, it broadcasts from Lebanon and is very suitable for providing an Islamic education to your children."
            );
            r4.setCreationDate(LocalDate.now());
            r4.setUser(user);
            r4.setVotes(22L);
            r4.setAngeRage(AgeRange.AGE_07_09);
            r4.setResourceType(ResourceType.URLS);
            r4.setSubject(subjectrepo.findByLabel("Islamic education").get());
            r4.addTopics(topicrepo.findByLabel("Morals").get());
            resourcerepo.save(r4);
            Resource r5 = new Resource();
            r5.setTitle("Fable - The Ant and the Grasshopper");
            r5.setActivated(true);
            r5.setDescription(
                "The Ant and the Grasshopper, the fable describes how a hungry grasshopper begs for food from an ant when winter comes. The situation sums up moral lessons about the virtues of hard work."
            );
            r5.setCreationDate(LocalDate.now());
            r5.setUser(user);
            r5.setVotes(8L);
            r5.setAngeRage(AgeRange.AGE_07_09);
            r5.setResourceType(ResourceType.URLS);
            r5.setSubject(subjectrepo.findByLabel("Arabic language").get());
            r5.addTopics(topicrepo.findByLabel("Reading").get());
            r5.addTopics(topicrepo.findByLabel("Morals").get());
            r5.addSkills(skillrepo.findByLabel("Reading").get());
            r5.addSkills(skillrepo.findByLabel("Reading comprehension").get());
            resourcerepo.save(r5);
        }

        //create 2 favorites if user exist
        Favorite favorite = favoriterepo.findById(1L).isPresent() ? favoriterepo.findById(1L).get() : null;
        Resource r1 = resourcerepo.findById(4L).isPresent() ? resourcerepo.findById(4L).get() : null;
        Resource r2 = resourcerepo.findById(4L).isPresent() ? resourcerepo.findById(5L).get() : null;
        if (favorite == null && user != null && r1 != null && r2 != null) {
            Favorite f1 = new Favorite();
            f1.setResource(r1);
            f1.user(user);
            f1.setCreationDate(LocalDate.now().minus(Period.ofDays(3)));
            favoriterepo.save(f1);
            Favorite f2 = new Favorite();
            f2.setResource(r2);
            f2.user(user);
            f2.setCreationDate(LocalDate.now().minus(Period.ofDays(2)));
            favoriterepo.save(f2);
        }

        //create 2 votes if user exist
        Votes votes = votesrepo.findById(1L).isPresent() ? votesrepo.findById(1L).get() : null;
        Resource r3 = resourcerepo.findById(4L).isPresent() ? resourcerepo.findById(1L).get() : null;
        Resource r4 = resourcerepo.findById(4L).isPresent() ? resourcerepo.findById(2L).get() : null;
        if (votes == null && user != null && r1 != null && r2 != null) {
            Votes v1 = new Votes();
            v1.setResource(r3);
            v1.user(user);
            votesrepo.save(v1);
            Votes v2 = new Votes();
            v2.setResource(r4);
            v2.user(user);
            votesrepo.save(v2);
        }
    }
}
