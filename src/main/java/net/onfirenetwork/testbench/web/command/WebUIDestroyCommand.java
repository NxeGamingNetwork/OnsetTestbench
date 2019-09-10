package net.onfirenetwork.testbench.web.command;

import lombok.*;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebUIDestroyCommand implements WebUICommand {
    @Getter
    transient String name = "destroy";
    @NonNull
    int id;
}
