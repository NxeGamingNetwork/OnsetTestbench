package net.onfirenetwork.testbench.web.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebUIExecuteCommand implements WebUICommand {
    @Getter
    transient String name = "execute";
    @NonNull
    int id;
    @NonNull
    String code;
}
