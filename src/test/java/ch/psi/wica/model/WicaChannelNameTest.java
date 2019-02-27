/*- Package Declaration ------------------------------------------------------*/
package ch.psi.wica.model;

/*- Imported packages --------------------------------------------------------*/

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

class WicaChannelNameTest
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/

   private final Logger logger = LoggerFactory.getLogger( WicaChannelNameTest.class );

/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/
/*- Class methods ------------------------------------------------------------*/
/*- Public methods -----------------------------------------------------------*/

   @Test
   void testProtocol()
   {
      assertEquals( WicaChannelName.Protocol.CA, WicaChannelName.Protocol.of( "ca://") );
      assertEquals( WicaChannelName.Protocol.PV, WicaChannelName.Protocol.of( "pv://") );
      assertThrows( IllegalArgumentException.class, () -> WicaChannelName.Protocol.of( "ca:" ) );
      assertThrows( IllegalArgumentException.class, () -> WicaChannelName.Protocol.of( "cA://" ) );
      assertThrows( IllegalArgumentException.class, () -> WicaChannelName.Protocol.of( "Ca://" ) );
      assertThrows( IllegalArgumentException.class, () -> WicaChannelName.Protocol.of( "CA://" ) );
      assertThrows( IllegalArgumentException.class, () -> WicaChannelName.Protocol.of( "PV://" ) );
   }

   @Test
   void test1()
   {
      WicaChannelName wicaChannelName = WicaChannelName.of( "abc" );
      assertEquals( ControlSystemName.of( "abc" ), wicaChannelName.getControlSystemName() );
      assertFalse( wicaChannelName.getProtocol().isPresent() );
      assertFalse( wicaChannelName.getInstance().isPresent() );
      assertEquals( "abc", wicaChannelName.asString() );
      logger.info( "WicaChannelName looks like this '{}' ", wicaChannelName.asString() );
   }

   @Test
   void test2()
   {
      WicaChannelName wicaChannelName = WicaChannelName.of( "abc##4" );
      assertEquals( ControlSystemName.of( "abc" ), wicaChannelName.getControlSystemName() );
      assertFalse( wicaChannelName.getProtocol().isPresent() );
      assertEquals( 4, wicaChannelName.getInstance().get().intValue() );
      assertEquals( "abc##4", wicaChannelName.asString() );
      logger.info( "WicaChannelName looks like this '{}' ", wicaChannelName.asString() );
   }

   @Test
   void test3()
   {
      WicaChannelName wicaChannelName = WicaChannelName.of( "ca://abc##00006" );
      assertEquals( ControlSystemName.of( "abc" ), wicaChannelName.getControlSystemName() );
      assertEquals( WicaChannelName.Protocol.CA, wicaChannelName.getProtocol().get() );
      assertEquals( 6, wicaChannelName.getInstance().get().intValue() );
      assertEquals( "ca://abc##00006", wicaChannelName.asString() );
      logger.info( "WicaChannelName looks like this '{}' ", wicaChannelName.asString() );
   }

   @Test
   void test4()
   {
      WicaChannelName wicaChannelName = WicaChannelName.of( "pv://abc##00006" );
      assertEquals( ControlSystemName.of( "abc" ), wicaChannelName.getControlSystemName() );
      assertEquals( WicaChannelName.Protocol.PV, wicaChannelName.getProtocol().get() );
      assertEquals( 6, wicaChannelName.getInstance().get().intValue() );
      assertEquals( "pv://abc##00006", wicaChannelName.asString() );
      logger.info( "WicaChannelName looks like this '{}' ", wicaChannelName.asString() );
   }

   @Test
   void test5()
   {
      WicaChannelName wicaChannelName = WicaChannelName.of( "abc##0" );
      assertEquals( ControlSystemName.of( "abc" ), wicaChannelName.getControlSystemName() );
      assertFalse( wicaChannelName.getProtocol().isPresent() );
      assertEquals( 0, wicaChannelName.getInstance().get().intValue()  );
      assertEquals( "abc##0", wicaChannelName.asString() );
      logger.info( "WicaChannelName looks like this '{}' ", wicaChannelName.asString() );
   }

   @Test
   void test6()
   {
      WicaChannelName wicaChannelName = WicaChannelName.of( "abc##1" );
      assertEquals( ControlSystemName.of( "abc" ), wicaChannelName.getControlSystemName() );
      assertFalse( wicaChannelName.getProtocol().isPresent() );
      assertEquals( 1, wicaChannelName.getInstance().get().intValue()  );
      assertEquals( "abc##1", wicaChannelName.asString() );
      logger.info( "WicaChannelName looks like this '{}' ", wicaChannelName.asString() );
   }

   /*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

}