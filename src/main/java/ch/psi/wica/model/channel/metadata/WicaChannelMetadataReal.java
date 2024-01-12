/*- Package Declaration ------------------------------------------------------*/
package ch.psi.wica.model.channel.metadata;

/*- Imported packages --------------------------------------------------------*/

import ch.psi.wica.model.channel.WicaChannelType;
import org.apache.commons.lang3.Validate;

/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

/**
 * Represents the metadata for a channel whose type is REAL.
 */
public class WicaChannelMetadataReal extends WicaChannelMetadata
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/

   private final String units;
   private final int precision;
   private final double upperDisplay;
   private final double lowerDisplay;
   private final double upperControl;
   private final double lowerControl;
   private final double upperAlarm;
   private final double lowerAlarm;
   private final double upperWarning;
   private final double lowerWarning;

/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/

   WicaChannelMetadataReal( WicaChannelType subType,
                            String units,
                            int precision,
                            double upperDisplay, double lowerDisplay,
                            double upperControl, double lowerControl,
                            double upperAlarm, double lowerAlarm,
                            double upperWarning, double lowerWarning )
   {
      super( subType );
      this.units = Validate.notNull( units, "The 'units' argument was null." );
      this.precision = precision;
      this.upperDisplay = upperDisplay;
      this.lowerDisplay = lowerDisplay;
      this.upperControl = upperControl;
      this.lowerControl = lowerControl;
      this.upperAlarm = upperAlarm;
      this.lowerAlarm = lowerAlarm;
      this.upperWarning = upperWarning;
      this.lowerWarning = lowerWarning;
   }

   public WicaChannelMetadataReal( String units,
                                   int precision,
                                   double upperDisplay, double lowerDisplay,
                                   double upperControl, double lowerControl,
                                   double upperAlarm, double lowerAlarm,
                                   double upperWarning, double lowerWarning )
   {
       this( WicaChannelType.REAL,
             units,
             precision,
             upperDisplay, lowerDisplay,
             upperControl, lowerControl,
             upperAlarm, lowerAlarm,
             upperWarning, lowerWarning );
   }

/*- Public methods -----------------------------------------------------------*/

   /**
    * Returns the Engineering Units.
    *
    * @return the value.
    */
   public String getUnits()
   {
      return units;
   }

   /**
    * Returns the Display Precision.
    *
    * @return the value.
    */
   public int getPrecision()
   {
      return precision;
   }

   /**
    * Returns the High Operating Range.
    *
    * @return the value.
    */
   public double getUpperDisplay()
   {
      return upperDisplay;
   }

   /**
    * Returns the Low Operating Range.
    *
    * @return the value.
    */
   public double getLowerDisplay()
   {
      return lowerDisplay;
   }

   /**
    * Returns the Drive High Control Limit.
    *
    * @return the value.
    */
   public double getUpperControl()
   {
      return upperControl;
   }

   /**
    * Returns the Drive Low Control Limit.
    *
    * @return the value.
    */
   public double getLowerControl()
   {
      return lowerControl;
   }

   /**
    * Returns the Upper Alarm limit.
    *
    * @return the value.
    */
   public double getUpperAlarm()
   {
      return upperAlarm;
   }

   /**
    * Returns the Lower Alarm Limit.
    *
    * @return the value.
    */
   public double getLowerAlarm()
   {
      return lowerAlarm;
   }

   /**
    * Returns the Upper Warning Limit.
    *
    * @return the value.
    */
   public double getUpperWarning()
   {
      return upperWarning;
   }

   /**
    * Returns the Lower Warning Limit.
    *
    * @return the value.
    */
   public double getLowerWarning()
   {
      return lowerWarning;
   }

/*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

}
