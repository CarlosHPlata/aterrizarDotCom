package com.aterrizar.service.checkin;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aterrizar.service.core.framework.strategy.CheckinCountryStrategy;
import com.neovisionaries.i18n.CountryCode;

@ExtendWith(MockitoExtension.class)
class CountryFactoryTest {

  private CheckinCountryStrategy strategy1;
  private CheckinCountryStrategy strategy2;
  private CheckinCountryStrategy defaultStrategy;
  private CheckinStrategyFactory countryFactory;

  @BeforeEach
  void setUp() {
    strategy1 = mock(CheckinCountryStrategy.class);
    strategy2 = mock(CheckinCountryStrategy.class);
    defaultStrategy = mock(CheckinCountryStrategy.class);

    when(strategy1.getCountryCode()).thenReturn(CountryCode.US);
    when(strategy2.getCountryCode()).thenReturn(CountryCode.CA);
    when(defaultStrategy.getCountryCode()).thenReturn(CountryCode.UNDEFINED);

    countryFactory = new CheckinStrategyFactory(List.of(strategy1, strategy2, defaultStrategy));
  }

  @Test
  void testInitWithNoDuplicates() {
    assertDoesNotThrow(() -> countryFactory.init());
  }

  @Test
  void testInitWithDuplicates() {
    CheckinCountryStrategy duplicateStrategy = mock(CheckinCountryStrategy.class);
    when(duplicateStrategy.getCountryCode()).thenReturn(CountryCode.US);

    assertThrows(
        IllegalStateException.class,
        () ->
            new CheckinStrategyFactory(
                List.of(strategy1, duplicateStrategy, strategy2, defaultStrategy)));
  }

  @Test
  void testGetServiceWithExistingCountryCode() {
    CheckinCountryStrategy service = countryFactory.getService(CountryCode.US);
    assertEquals(strategy1, service);
  }

  @Test
  void testGetServiceWithUndefinedCountryCode() {
    CheckinCountryStrategy service = countryFactory.getService(CountryCode.UNDEFINED);
    assertEquals(defaultStrategy, service);
  }

  @Test
  void testGetServiceWithNonExistentCountryCode() {
    CheckinCountryStrategy service = countryFactory.getService(CountryCode.FR);
    assertEquals(defaultStrategy, service);
  }
}
