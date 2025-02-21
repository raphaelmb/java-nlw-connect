package br.com.nlw.events.service;

import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.UserReferrerNotFoundException;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repository.EventRepository;
import br.com.nlw.events.repository.SubscriptionRepository;
import br.com.nlw.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {
        Event event = eventRepository.findByPrettyName(eventName);
        if (event == null) {
            throw new EventNotFoundException("Event " + eventName + " not found");
        }
        User userFound = userRepository.findByEmail(user.getEmail());
        if (userFound == null) {
            userFound = userRepository.save(user);
        }

        User referrer = userRepository.findById(userId).orElse(null);
        if (referrer == null) {
            throw new UserReferrerNotFoundException("User with id " + userId + " not found");
        }

        Subscription subscription = new Subscription();
        subscription.setEvent(event);
        subscription.setSubscriber(userFound);
        subscription.setIndication(referrer);

        Subscription tempSubscription = subscriptionRepository.findByEventAndSubscriber(event, userFound);
        if (tempSubscription != null) {
            throw new SubscriptionConflictException("There is already a subscription to user " + userFound.getName() + " in the event " + event.getTitle());
        }

        Subscription result = subscriptionRepository.save(subscription);

        return new SubscriptionResponse(result.getSubscriptionNumber(), "http://codecraft.com/subscription" + result.getEvent().getPrettyName()+"/"+ result.getSubscriber().getId());
    }

}
