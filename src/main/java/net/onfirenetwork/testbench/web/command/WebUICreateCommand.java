package net.onfirenetwork.testbench.web.command;

import lombok.*;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebUICreateCommand implements WebUICommand {
    @Getter
    transient String name = "create";
    @NonNull
    int id;
    @NonNull
    int x;
    @NonNull
    int y;
    @NonNull
    int width;
    @NonNull
    int height;
}
