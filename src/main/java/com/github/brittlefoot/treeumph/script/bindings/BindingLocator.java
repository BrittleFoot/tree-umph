package com.github.brittlefoot.treeumph.script.bindings;

import com.github.brittlefoot.treeumph.script.NamingStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class BindingLocator {

    private static BindingLocator INSTANCE;

    public static BindingLocator getInstance() {
        return INSTANCE;
    }

    BindingLocator() {
        INSTANCE = this;
    }

    @Autowired
    private List<IServiceBinding> availableBindings;

    public IServiceBinding get(String bindingName, NamingStyle namingStyle) {
        if (availableBindings == null) availableBindings = new ArrayList<>();
        return availableBindings.stream()
                .filter(sb -> namingStyle.apply(sb.getName()).equals(bindingName))
                .findFirst().orElse(null);
    }

    public List<String> getAvailableBindings(NamingStyle namingStyle) {
        return availableBindings.stream()
                .map(IServiceBinding::getName)
                .map(namingStyle)
                .collect(Collectors.toList());
    }

}
