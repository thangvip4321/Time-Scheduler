import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import com.cronutils.model.Cron;
import com.cronutils.model.SingleCron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.field.CronField;
import com.cronutils.model.field.CronFieldName;
import com.cronutils.model.field.expression.FieldExpression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CronTest {
    private Cron cron;
    private CronFieldName testName;
    private List<CronField> fields;
    @Mock
    private CronField mockField;

    @BeforeEach
    public void setUp() {
        System.out.println("lmao");

        MockitoAnnotations.openMocks(this);
        testName = CronFieldName.SECOND;
        when(mockField.getField()).thenReturn(testName);
        fields = Collections.singletonList(mockField);
        cron = new SingleCron(mock(CronDefinition.class), fields);
    }

    @Test
    public void testAsString() {
        final String expressionString = "somestring";
        final FieldExpression mockFieldExpression = mock(FieldExpression.class);
        when(mockField.getExpression()).thenReturn(mockFieldExpression);
        when(mockFieldExpression.asString()).thenReturn(expressionString);
        assertEquals(expressionString, cron.asString());
    }
}
