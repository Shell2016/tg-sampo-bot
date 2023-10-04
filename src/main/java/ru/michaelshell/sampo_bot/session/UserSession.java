package ru.michaelshell.sampo_bot.session;

import lombok.*;
import ru.michaelshell.sampo_bot.database.entity.Role;
import ru.michaelshell.sampo_bot.database.entity.Status;

import java.io.Serializable;
import java.util.*;

@Builder
@Setter
@Getter
public class UserSession implements Serializable {
    private final long id;
    private boolean isAuthenticated;
    private State state;
    private Status userStatus;
    private Role userRole;
    private final Map<SessionAttribute, Object> attributes = new EnumMap<>(SessionAttribute.class);

    public Object getAttribute(SessionAttribute attribute) {
        return attributes.getOrDefault(attribute, null);
    }

    public void setAttribute(SessionAttribute attribute, Object object) {
        attributes.put(attribute, object);
    }

    public void removeAttribute(SessionAttribute attribute) {
        attributes.remove(attribute);
    }

    public void clearAttributes() {
        attributes.clear();
    }

    public void setDefaultState() {
        this.state = State.DEFAULT;
    }

    public boolean hasRole() {
        return userRole != null;
    }
}
