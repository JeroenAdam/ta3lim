package com.ta3lim.app;

import com.ta3lim.app.domain.Favorite;
import com.ta3lim.app.domain.Message;
import com.ta3lim.app.domain.Notification;
import com.ta3lim.app.domain.Resource;
import com.ta3lim.app.domain.Skill;
import com.ta3lim.app.domain.Subject;
import com.ta3lim.app.domain.Topic;
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
import java.util.Arrays;
import java.util.List;
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
        System.out.println("Starting import demo data... please assure user & admin already exist.");
        // fetch all resources
        for (Resource resource : resourcerepo.findAll()) {
            log.info("Found " + resource.toString());
        }

        //set admin profile info
        if (userrepo.findOneByLogin("admin@localhost").isPresent() && userextendedrepo.findById(1L).isPresent() == false) {
            UserExtended u1 = new UserExtended();
            u1.setAboutMe("I'm a homeschooling enthousiast, following the unschooling philosophy");
            u1.setOccupation("IT generalist, Software Developer");
            u1.setSocialMedia("https://www.adambahri.com");
            u1.setCivilStatus(CivilStatus.MARRIED);
            u1.setFirstchild(Children.AGE_04_06);
            u1.setSecondchild(Children.AGE_07_09);
            u1.setFilesquota(200);
            u1.setUser(userrepo.findOneByLogin("admin@localhost").get());
            userextendedrepo.save(u1);
        }

        //create first 6 subjects
        if (subjectrepo.findById(1L).isPresent() == false) {
            subjectrepo.save(new Subject("Arabic language", LocalDate.now()));
            subjectrepo.save(new Subject("Islamic education", LocalDate.now()));
            subjectrepo.save(new Subject("History", LocalDate.now()));
            subjectrepo.save(new Subject("Mathematics", LocalDate.now()));
            subjectrepo.save(new Subject("IT", LocalDate.now()));
            subjectrepo.save(new Subject("Knowledge", LocalDate.now()));
        }

        //create first 4 topics
        if (topicrepo.findById(1L).isPresent() == false) {
            topicrepo.save(new Topic("Reading", LocalDate.now()));
            topicrepo.save(new Topic("Writing", LocalDate.now()));
            topicrepo.save(new Topic("Arithmetic", LocalDate.now()));
            topicrepo.save(new Topic("Morals", LocalDate.now()));
        }

        //create first 3 skills
        if (skillrepo.findById(1L).isPresent() == false) {
            skillrepo.save(new Skill("Reading", LocalDate.now()));
            skillrepo.save(new Skill("Reading comprehension", LocalDate.now()));
            skillrepo.save(new Skill("Counting/subtracting", LocalDate.now()));
            skillrepo.save(new Skill("Programming", LocalDate.now()));
        }

        //create 5 resources if user/subject/topic/skill exist
        bulkResourceImporter();

        //create first notification if user exists
        if (userrepo.findOneByLogin("user").isPresent() && notificationrepo.findById(1L).isPresent() == false) {
            notificationrepo.save(
                new Notification(
                    "You have 1 unread message",
                    NotificationType.UNREAD_MESSAGES,
                    userrepo.findOneByLogin("user").get(),
                    true,
                    LocalDate.now()
                )
            );
        }

        //create 2 messages if user & admin exist
        if (
            messagerepo.findById(1L).isPresent() == false &&
            userrepo.findOneByLogin("user").isPresent() &&
            userrepo.findOneByLogin("admin@localhost").isPresent()
        ) {
            messagerepo.save(
                new Message(
                    "First message",
                    LocalDate.now().minus(Period.ofDays(5)),
                    true,
                    userrepo.findOneByLogin("admin@localhost").get(),
                    userrepo.findOneByLogin("user").get()
                )
            );
            messagerepo.save(
                new Message(
                    "I have a question, can I ...",
                    LocalDate.now().minus(Period.ofDays(3)),
                    true,
                    userrepo.findOneByLogin("user").get(),
                    userrepo.findOneByLogin("admin@localhost").get()
                )
            );
        }

        //create 2 favorites if user & resources exist
        if (
            favoriterepo.findById(1L).isPresent() == false &&
            userrepo.findOneByLogin("user").isPresent() &&
            resourcerepo.findById(5L).isPresent()
        ) {
            favoriterepo.save(
                new Favorite(
                    userrepo.findOneByLogin("user").get(),
                    resourcerepo.findById(4L).get(),
                    LocalDate.now().minus(Period.ofDays(3))
                )
            );
            favoriterepo.save(
                new Favorite(
                    userrepo.findOneByLogin("user").get(),
                    resourcerepo.findById(5L).get(),
                    LocalDate.now().minus(Period.ofDays(2))
                )
            );
        }

        //create 2 votes if user exist
        if (
            votesrepo.findById(1L).isPresent() == false &&
            userrepo.findOneByLogin("user").isPresent() &&
            resourcerepo.findById(5L).isPresent()
        ) {
            votesrepo.save(new Votes(userrepo.findOneByLogin("user").get(), resourcerepo.findById(1L).get()));
            votesrepo.save(new Votes(userrepo.findOneByLogin("user").get(), resourcerepo.findById(2L).get()));
        }
    }

    private void bulkResourceImporter() {
        if (
            resourcerepo.findById(1L).isPresent() == false &&
            userrepo.findOneByLogin("user").isPresent() &&
            subjectrepo.findById(1L).isPresent() &&
            topicrepo.findById(1L).isPresent()
        ) {
            Resource r1 = new Resource();
            r1.setTitle("TuxMath - free game");
            r1.setActivated(true);
            r1.setDescription(
                "TuxMath is an open source, free game whose difficulty is appropriate for students from elementary to high school, in other words, 7 to 13 years."
            );
            r1.setCreationDate(LocalDate.now());
            r1.setUser(userrepo.findOneByLogin("user").get());
            r1.setVotes(202L);
            r1.setAngeRage(AgeRange.AGE_07_09);
            r1.setResourceType(ResourceType.URLS);
            r1.setSubject(subjectrepo.findByLabel("Mathematics").get());
            r1.addTopics(topicrepo.findByLabel("Arithmetic").get());
            r1.addSkills(skillrepo.findByLabel("Counting/subtracting").get());
            Resource r2 = new Resource();
            r2.setTitle("'Iqra' textbooks");
            r2.setActivated(true);
            r2.setDescription(
                "The first series of Arabic textbooks in Morocco. 'Iqra', published by Ahmed Boukmakh. It consists of 5 volumes and these have been made available for download. Highly recommended for learning to read."
            );
            r2.setCreationDate(LocalDate.now());
            r2.setUser(userrepo.findOneByLogin("user").get());
            r2.setVotes(57L);
            r2.setAngeRage(AgeRange.AGE_10_12);
            r2.setResourceType(ResourceType.URLS);
            r2.setSubject(subjectrepo.findByLabel("Arabic language").get());
            r2.addTopics(topicrepo.findByLabel("Reading").get());
            r2.addTopics(topicrepo.findByLabel("Writing").get());
            r2.addSkills(skillrepo.findByLabel("Reading").get());
            Resource r3 = new Resource();
            r3.setTitle("Ibn Battuta, Traveler from Tangier");
            r3.setActivated(true);
            r3.setDescription(
                "In the year 1349 a dusty Arab horseman rode slowly toward the city of Tangier on the North African coast. For Ibn Battuta, it was the end of a long journey."
            );
            r3.setCreationDate(LocalDate.now());
            r3.setUser(userrepo.findOneByLogin("user").get());
            r3.setVotes(15L);
            r3.setAngeRage(AgeRange.AGE_10_12);
            r3.setResourceType(ResourceType.DOCUMENTS);
            r3.setSubject(subjectrepo.findByLabel("History").get());
            r3.addTopics(topicrepo.findByLabel("Reading").get());
            r3.addSkills(skillrepo.findByLabel("Reading").get());
            r3.addSkills(skillrepo.findByLabel("Reading comprehension").get());
            Resource r4 = new Resource();
            r4.setTitle("Taha TV for Kids");
            r4.setActivated(true);
            r4.setDescription(
                "Taha TV for Kids is available on Youtube, it broadcasts from Lebanon and is very suitable for providing an Islamic education to your children."
            );
            r4.setCreationDate(LocalDate.now());
            r4.setUser(userrepo.findOneByLogin("user").get());
            r4.setVotes(22L);
            r4.setAngeRage(AgeRange.AGE_07_09);
            r4.setResourceType(ResourceType.URLS);
            r4.setSubject(subjectrepo.findByLabel("Islamic education").get());
            r4.addTopics(topicrepo.findByLabel("Morals").get());
            Resource r5 = new Resource();
            r5.setTitle("Fable - The Ant and the Grasshopper");
            r5.setActivated(true);
            r5.setDescription(
                "The Ant and the Grasshopper, the fable describes how a hungry grasshopper begs for food from an ant when winter comes. The situation sums up moral lessons about the virtues of hard work."
            );
            r5.setCreationDate(LocalDate.now());
            r5.setUser(userrepo.findOneByLogin("user").get());
            r5.setVotes(8L);
            r5.setAngeRage(AgeRange.AGE_07_09);
            r5.setResourceType(ResourceType.URLS);
            r5.setSubject(subjectrepo.findByLabel("Arabic language").get());
            r5.addTopics(topicrepo.findByLabel("Reading").get());
            r5.addTopics(topicrepo.findByLabel("Morals").get());
            r5.addSkills(skillrepo.findByLabel("Reading").get());
            r5.addSkills(skillrepo.findByLabel("Reading comprehension").get());
            Resource r6 = new Resource();
            r6.setTitle("About reading age");
            r6.setActivated(true);
            r6.setDescription(
                "When my daughter turned 6, I wanted her to get started with reading. But then I found out on sandradodd.com that the average age homeschooled children learnt to read is about 8 years."
            );
            r6.setCreationDate(LocalDate.now());
            r6.setUser(userrepo.findOneByLogin("user").get());
            r6.setVotes(8L);
            r6.setAngeRage(AgeRange.AGE_07_09);
            r6.setResourceType(ResourceType.URLS);
            r6.setUrl("https://sandradodd.com/readingage.html");
            r6.setSubject(subjectrepo.findByLabel("Arabic language").get());
            r6.addTopics(topicrepo.findByLabel("Reading").get());
            r6.addSkills(skillrepo.findByLabel("Reading").get());
            Resource r7 = new Resource();
            r7.setTitle("Corsairs in the former Republic of Salé");
            r7.setActivated(true);
            r7.setDescription(
                "Published for the first time in 1948, Commander Coindreau's book remains one of the best summaries of the history of the Moroccan piracy and more particularly that of the corsairs of Rabat-Salé."
            );
            r7.setCreationDate(LocalDate.now());
            r7.setUser(userrepo.findOneByLogin("user").get());
            r7.setVotes(8L);
            r7.setAngeRage(AgeRange.AGE_ALL);
            r7.setResourceType(ResourceType.URLS);
            r7.setUrl("https://www.amazon.fr/Corsaires-Sal%C3%A9-Roger-Coindreau/dp/9981896764");
            r7.setSubject(subjectrepo.findByLabel("History").get());
            r7.addTopics(topicrepo.findByLabel("Reading").get());
            r7.addSkills(skillrepo.findByLabel("Reading comprehension").get());
            Resource r8 = new Resource();
            r8.setTitle("Learn programming");
            r8.setActivated(true);
            r8.setDescription(
                "Advice: try to facilitate your children to learn programming apps - websites or robots. Whether through the clubs 'to their finds' or from YouTube channels... Programming and IT in general are essential in this age."
            );
            r8.setCreationDate(LocalDate.now());
            r8.setUser(userrepo.findOneByLogin("user").get());
            r8.setVotes(1L);
            r8.setAngeRage(AgeRange.AGE_ALL);
            r8.setResourceType(ResourceType.ANNOUNCEMENTS);
            r8.setSubject(subjectrepo.findByLabel("IT").get());
            r8.addTopics(topicrepo.findByLabel("Reading").get());
            r8.addSkills(skillrepo.findByLabel("Programming").get());
            Resource r9 = new Resource();
            r9.setTitle("Ibn Khaldoun on pedagogy");
            r9.setActivated(true);
            r9.setDescription(
                "Ibn Khaldun’s philosophy of education: Education is the key imperative for human development. About Ibn Khaldun’s concept of education as found in his book ‘Al Muqaddimah’."
            );
            r9.setCreationDate(LocalDate.now());
            r9.setUser(userrepo.findOneByLogin("user").get());
            r9.setVotes(2L);
            r9.setAngeRage(AgeRange.AGE_ALL);
            r9.setResourceType(ResourceType.URLS);
            r9.setUrl("https://muslimheritage.com/ibn-khalduns-education-muqaddima");
            r9.setSubject(subjectrepo.findByLabel("Knowledge").get());
            r9.addTopics(topicrepo.findByLabel("Morals").get());
            Resource r10 = new Resource();
            r10.setTitle("John Holt quote");
            r10.setActivated(true);
            r10.setDescription(
                "We do things backwards. We think in terms of getting a skill first, and then finding useful and interesting things to do with it. The sensible way, the best way, is to start with something worth doing, and then, moved by a strong desire to do it, get whatever skills are needed."
            );
            r10.setCreationDate(LocalDate.now());
            r10.setUser(userrepo.findOneByLogin("user").get());
            r10.setVotes(2L);
            r10.setAngeRage(AgeRange.AGE_ALL);
            r10.setResourceType(ResourceType.ANNOUNCEMENTS);
            r10.setSubject(subjectrepo.findByLabel("Knowledge").get());
            Resource r11 = new Resource();
            r11.setTitle("Arabic Collections Online");
            r11.setActivated(true);
            r11.setDescription(
                "A publicly available digital library of public domain Arabic language content. Currently providing access to +17,000 volumes across +10,000 subjects drawn from rich Arabic collections of distinguished research libraries."
            );
            r11.setCreationDate(LocalDate.now());
            r11.setUser(userrepo.findOneByLogin("user").get());
            r11.setVotes(2L);
            r11.setAngeRage(AgeRange.AGE_ALL);
            r11.setResourceType(ResourceType.URLS);
            r11.setUrl("http://dlib.nyu.edu/aco/");
            r11.setSubject(subjectrepo.findByLabel("Arabic language").get());
            r11.addTopics(topicrepo.findByLabel("Reading").get());
            r11.addSkills(skillrepo.findByLabel("Reading").get());
            Resource r12 = new Resource();
            r12.setTitle("Largest-ever Arab literacy initiative");
            r12.setActivated(true);
            r12.setDescription(
                "An initiative launched by the ruler of Dubai, Sheikh Mohammed bin Rashid Al Maktoum, to encourage students to read,. He challenges students to read as many books as possible (over 50) in one academic year. See url attached."
            );
            r12.setCreationDate(LocalDate.now());
            r12.setUser(userrepo.findOneByLogin("user").get());
            r12.setVotes(2L);
            r12.setAngeRage(AgeRange.AGE_ALL);
            r12.setResourceType(ResourceType.ANNOUNCEMENTS);
            r12.setUrl("https://arabreadingchallenge.com");
            r12.setSubject(subjectrepo.findByLabel("Arabic language").get());
            r12.addTopics(topicrepo.findByLabel("Reading").get());
            r12.addSkills(skillrepo.findByLabel("Reading").get());
            List<Resource> r = Arrays.asList(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12);
            resourcerepo.saveAll(r);
        }
    }
}
