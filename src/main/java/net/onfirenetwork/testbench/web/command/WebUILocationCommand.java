package net.onfirenetwork.testbench.web.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebUILocationCommand implements WebUICommand {
    @Getter
    transient String name = "location";
    @NonNull
    int id;
    @NonNull
    int x;
    @NonNull
    int y;
}
