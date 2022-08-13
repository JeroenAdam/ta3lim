import resource from 'app/entities/resource/resource.reducer';
import subject from 'app/entities/subject/subject.reducer';
import topic from 'app/entities/topic/topic.reducer';
import skill from 'app/entities/skill/skill.reducer';
import votes from 'app/entities/votes/votes.reducer';
import notification from 'app/entities/notification/notification.reducer';
import message from 'app/entities/message/message.reducer';
import favorite from 'app/entities/favorite/favorite.reducer';
import userExtended from 'app/entities/user-extended/user-extended.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  resource,
  subject,
  topic,
  skill,
  votes,
  notification,
  message,
  favorite,
  userExtended,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
