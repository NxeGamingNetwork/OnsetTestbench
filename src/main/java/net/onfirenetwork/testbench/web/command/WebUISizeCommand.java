package net.onfirenetwork.testbench.web.command;

import lombok.*;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebUISizeCommand implements WebUICommand {
    @Getter
    transient String name = "size";
    @NonNull
    int id;
    @NonNull
    int width;
    @NonNull
    int height;
}
