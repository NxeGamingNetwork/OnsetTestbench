package net.onfirenetwork.testbench.web.command;

import lombok.*;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebUILoadCommand implements WebUICommand {
    @Getter
    transient String name = "load";
    @NonNull
    int id;
    @NonNull
    String url;
}
