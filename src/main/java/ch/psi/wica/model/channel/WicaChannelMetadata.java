/*- Package Declaration ------------------------------------------------------*/
package ch.psi.wica.model.channel;

/*- Imported packages --------------------------------------------------------*/

import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;

/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

/**
 * Represents the root of a typed hierarchy of objects which describe the
 * static properties of a control point, ie those properties which may be
 * readout when the channel comes online and which thereafter remain
 * constant.
 *
 * Typically these properties describe the basic underlying nature of the
 * control point, for example, the physical quantity that the control
 * point represents, the allowed operating limits and/or the values which
 * correspond to error or warning conditions.
 */
@Immutable
public abstract class WicaChannelMetadata extends WicaChannelData
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/
/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/

   public WicaChannelMetadata( WicaChannelType wicaChannelType )
   {
      super( wicaChannelType, LocalDateTime.now() );
   }

/*- Class methods ------------------------------------------------------------*/

   public static WicaChannelMetadata.WicaChannelMetadataUnknown createUnknownInstance() { return new WicaChannelMetadataUnknown(); }

   public static WicaChannelMetadata.WicaChannelMetadataString createStringInstance()
   {
      return new WicaChannelMetadataString();
   }
   public static WicaChannelMetadata.WicaChannelMetadataStringArray createStringArrayInstance()
   {
      return new WicaChannelMetadataStringArray();
   }

   public static WicaChannelMetadata.WicaChannelMetadataInteger createIntegerInstance( String units,
                                                                  int upperDisplay, int lowerDisplay,
                                                                  int upperControl, int lowerControl,
                                                                  int upperAlarm, int lowerAlarm,
                                                                  int upperWarning, int lowerWarning )
   {
      return new WicaChannelMetadataInteger( units, upperDisplay, lowerDisplay, upperControl, lowerControl,
                                             upperAlarm, lowerAlarm, upperWarning, lowerWarning );
   }

   public static WicaChannelMetadata.WicaChannelMetadataIntegerArray createIntegerArrayInstance( String units,
                                                                 int upperDisplay, int lowerDisplay,
                                                                 int upperControl, int lowerControl,
                                                                 int upperAlarm, int lowerAlarm,
                                                                 int upperWarning, int lowerWarning )
   {
      return new WicaChannelMetadataIntegerArray(units, upperDisplay, lowerDisplay, upperControl, lowerControl,
                                                 upperAlarm, lowerAlarm, upperWarning, lowerWarning );
   }

   public static WicaChannelMetadata.WicaChannelMetadataReal createRealInstance( String units,
                                                         int precision,
                                                         double upperDisplay, double lowerDisplay,
                                                         double upperControl, double lowerControl,
                                                         double upperAlarm, double lowerAlarm,
                                                         double upperWarning, double lowerWarning )
   {
      return new WicaChannelMetadataReal( units, precision, upperDisplay, lowerDisplay, upperControl, lowerControl,
                                         upperAlarm, lowerAlarm, upperWarning, lowerWarning );
   }

   public static WicaChannelMetadata.WicaChannelMetadataRealArray createRealArrayInstance( String units,
                                                              int precision,
                                                              double upperDisplay, double lowerDisplay,
                                                              double upperControl, double lowerControl,
                                                              double upperAlarm, double lowerAlarm,
                                                              double upperWarning, double lowerWarning )
   {
      return new WicaChannelMetadataRealArray( units, precision, upperDisplay, lowerDisplay, upperControl, lowerControl,
                                              upperAlarm, lowerAlarm, upperWarning, lowerWarning );
   }

/*- Public methods -----------------------------------------------------------*/
/*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

/*- Nested Class: WicaChannelMetadataUnknown ---------------------------------*/

   public static class WicaChannelMetadataUnknown extends WicaChannelMetadata
   {
      public WicaChannelMetadataUnknown()
      {
         super( WicaChannelType.UNKNOWN );
      }
   }


/*- Nested Class: WicaChannelMetadataString ----------------------------------*/

   public static class WicaChannelMetadataString extends WicaChannelMetadata
   {
      public WicaChannelMetadataString()
      {
         super( WicaChannelType.STRING );
      }
   }


/*- Nested Class: WicaChannelMetadataStringArray -----------------------------*/

   public static class WicaChannelMetadataStringArray extends WicaChannelMetadata
   {
      public WicaChannelMetadataStringArray()
      {
         super( WicaChannelType.STRING_ARRAY );
      }
   }


/*- Nested Class: WicaChannelMetadataInteger ---------------------------------*/

   public static class WicaChannelMetadataInteger extends WicaChannelMetadata
   {
      private final String units;
      private final int upperDisplay;
      private final int lowerDisplay;
      private final int upperControl;
      private final int lowerControl;
      private final int upperAlarm;
      private final int lowerAlarm;
      private final int upperWarning;
      private final int lowerWarning;

      @SuppressWarnings( "Duplicates" )
      WicaChannelMetadataInteger( WicaChannelType subType,
                                  String units,
                                  int upperDisplay, int lowerDisplay,
                                  int upperControl, int lowerControl,
                                  int upperAlarm, int lowerAlarm,
                                  int upperWarning, int lowerWarning )
      {
         super( subType );
         this.units = Validate.notNull( units );
         this.upperDisplay = upperDisplay;
         this.lowerDisplay = lowerDisplay;
         this.upperControl = upperControl;
         this.lowerControl = lowerControl;
         this.upperAlarm   = upperAlarm;
         this.lowerAlarm   = lowerAlarm;
         this.upperWarning = upperWarning;
         this.lowerWarning = lowerWarning;
      }

      public WicaChannelMetadataInteger( String units,
                                          int upperDisplay, int lowerDisplay,
                                          int upperControl, int lowerControl,
                                          int upperAlarm, int lowerAlarm,
                                          int upperWarning, int lowerWarning )
      {
         this( WicaChannelType.INTEGER,
               units,
               upperDisplay, lowerDisplay,
               upperControl, lowerControl,
               upperAlarm, lowerAlarm,
               upperWarning, lowerWarning );
      }

      // Engineering Units
      public String getUnits()
      {
         return units;
      }

      // High Operating Range
      public int getUpperDisplay()
      {
         return upperDisplay;
      }

      // Low Operating Range
      public int getLowerDisplay()
      {
         return lowerDisplay;
      }

      // Drive High Control Limit
      public int getUpperControl()
      {
         return upperControl;
      }

      // Drive Low Control Limit
      public int getLowerControl()
      {
         return lowerControl;
      }

      // Upper Alarm limit
      public int getUpperAlarm()
      {
         return upperAlarm;
      }

      // Lower Alarm Limit
      public int getLowerAlarm()
      {
         return lowerAlarm;
      }

      // Upper Warning Limit
      public int getUpperWarning()
      {
         return upperWarning;
      }

      // Lower Warning Limit
      public int getLowerWarning()
      {
         return lowerWarning;
      }
   }


/*- Nested Class: WicaChannelMetadataIntegerArray ----------------------------*/

   public static class WicaChannelMetadataIntegerArray extends WicaChannelMetadataInteger
   {
      WicaChannelMetadataIntegerArray( String units,
                                       int upperDisplay, int lowerDisplay,
                                       int upperControl, int lowerControl,
                                       int upperAlarm, int lowerAlarm,
                                       int upperWarning, int lowerWarning )
      {
         super( WicaChannelType.INTEGER_ARRAY, units, upperDisplay, lowerDisplay, upperControl, lowerControl, upperAlarm, lowerAlarm, upperWarning, lowerWarning );
      }
   }


/*- Nested Class: WicaChannelMetadataReal ------------------------------------*/

   public static class WicaChannelMetadataReal extends WicaChannelMetadata
   {
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

      @SuppressWarnings( "Duplicates" )
      WicaChannelMetadataReal( WicaChannelType subType,
                               String units,
                               int precision,
                               double upperDisplay, double lowerDisplay,
                               double upperControl, double lowerControl,
                               double upperAlarm, double lowerAlarm,
                               double upperWarning, double lowerWarning )
      {
         super( subType );
         this.units = Validate.notNull( units );
         this.precision = precision;
         this.upperDisplay = upperDisplay;
         this.lowerDisplay = lowerDisplay;
         this.upperControl = upperControl;
         this.lowerControl = lowerControl;
         this.upperAlarm =   upperAlarm;
         this.lowerAlarm =   lowerAlarm;
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

      // Engineering Units
      public String getUnits()
      {
         return units;
      }

      // Display Precision
      public int getPrecision()
      {
         return precision;
      }

      // High Operating Range
      public double getUpperDisplay()
      {
         return upperDisplay;
      }

      // Low Operating Range
      public double getLowerDisplay()
      {
         return lowerDisplay;
      }

      // Drive High Control Limit
      public double getUpperControl()
      {
         return upperControl;
      }

      // Drive Low Control Limit
      public double getLowerControl()
      {
         return lowerControl;
      }

      // Upper Alarm limit
      public double getUpperAlarm()
      {
         return upperAlarm;
      }

      // Lower Alarm Limit
      public double getLowerAlarm()
      {
         return lowerAlarm;
      }

      // Upper Warning Limit
      public double getUpperWarning()
      {
         return upperWarning;
      }

      // Lower Warning Limit
      public double getLowerWarning()
      {
         return lowerWarning;
      }
   }


/*- Nested Class: WicaChannelMetadataRealArray -------------------------------*/

   public static class WicaChannelMetadataRealArray extends WicaChannelMetadataReal
   {
      WicaChannelMetadataRealArray( String units,
                                           int precision,
                                           double upperDisplay, double lowerDisplay,
                                           double upperControl, double lowerControl,
                                           double upperAlarm,   double lowerAlarm,
                                           double upperWarning, double lowerWarning )
      {
         super( WicaChannelType.REAL_ARRAY, units, precision, upperDisplay, lowerDisplay, upperControl, lowerControl, upperAlarm, lowerAlarm, upperWarning, lowerWarning );
      }
   }
}