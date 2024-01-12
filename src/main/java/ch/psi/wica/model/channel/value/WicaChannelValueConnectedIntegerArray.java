/*- Package Declaration ------------------------------------------------------*/

package ch.psi.wica.model.channel.value;

/*- Imported packages --------------------------------------------------------*/

import ch.psi.wica.model.channel.WicaChannelType;
import java.time.LocalDateTime;
import java.util.Arrays;

/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

/**
 * Represents the value of a connected channel whose underlying type is INTEGER_ARRAY.
 */
public class WicaChannelValueConnectedIntegerArray extends WicaChannelValueConnected
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/

   private final int[] value;


/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/

   /**
    * Constructs a new instance of a value for a channel whose type is INTEGER_ARRAY.
    *
    * @param alarmSeverity the alarm severity.
    * @param alarmStatus the alarm status.
    * @param dataSourceTimestamp the data source timestamp.
    * @param value the value.
    */
   public WicaChannelValueConnectedIntegerArray( WicaChannelAlarmSeverity alarmSeverity, WicaChannelAlarmStatus alarmStatus, LocalDateTime dataSourceTimestamp, int[] value )
   {
      super( WicaChannelType.INTEGER_ARRAY, alarmSeverity, alarmStatus, dataSourceTimestamp );
      this.value = value;
   }

/*- Class methods ------------------------------------------------------------*/
/*- Public methods -----------------------------------------------------------*/

   /**
    * Returns the value of the channel.
    *
    * @return the value.
    */
   public int[] getValue()
   {
      return value;
   }

   @Override
   public String toString()
   {
      return "WicaChannelValueConnectedIntegerArray{" +
              "value=" + Arrays.toString( value ) +
              '}';
   }

/*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

}
