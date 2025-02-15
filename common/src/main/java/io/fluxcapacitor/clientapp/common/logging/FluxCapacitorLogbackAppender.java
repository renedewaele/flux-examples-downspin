package io.fluxcapacitor.clientapp.common.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import io.fluxcapacitor.common.api.Metadata;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.common.serialization.DeserializingMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Slf4j
public class FluxCapacitorLogbackAppender extends AppenderBase<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent event) {
        try {
            Optional<Throwable> throwable =
                    ofNullable((ThrowableProxy) event.getThrowableProxy()).map(ThrowableProxy::getThrowable);
            Metadata metadata = ofNullable(DeserializingMessage.getCurrent()).map(
                    d -> d.getSerializedObject().getMetadata()).orElse(Metadata.empty());
            metadata = metadata.with(
                    "stackTrace", format("[%s] %s %s - %s%s", event.getThreadName(), event.getLevel(),
                                         event.getLoggerName(), event.getFormattedMessage(),
                                         throwable.map(e -> "\n" + getStackTrace(e)).orElse("")),
                    "level", event.getLevel().toString(),
                    "loggerName", event.getLoggerName());
            if (throwable.isPresent()) {
                Throwable e = throwable.get();
                metadata = metadata.with(
                        "error", e.getClass().getSimpleName(),
                        "errorMessage", isBlank(e.getMessage()) ? event.getFormattedMessage() : e.getMessage());
                StackTraceElement[] stackTraceElements =
                        ofNullable(e.getStackTrace()).filter(s -> s.length > 0).orElse(null);
                if (stackTraceElements != null) {
                    metadata = metadata.with(
                            "traceElement", stackTraceElements[0].toString());
                }
            } else {
                metadata = metadata.with(
                        "errorMessage", event.getFormattedMessage());
            }
            FluxCapacitor.get().errorGateway().report(
                    event.getLevel() == Level.WARN ? new ConsoleWarning() : new ConsoleError(), metadata);
        } catch (Throwable e) {
            log.info("Failed to publish console error", e);
        }
    }

}
