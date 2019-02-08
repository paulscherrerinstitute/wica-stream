/*- Package Declaration ------------------------------------------------------*/
package ch.psi.wica.infrastructure;

/*- Imported packages --------------------------------------------------------*/
import ch.psi.wica.model.*;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Integer.parseInt;

/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

@Immutable
public class WicaChannelDataNumericScaleSupplier implements NumericScaleSupplier
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/

   private final Logger logger = LoggerFactory.getLogger(WicaStreamConfigurationDecoder.class );

   private final WicaStream wicaStream;
   private final WicaStreamProperties wicaStreamProperties;
   private final Set<WicaChannel> wicaChannels;
   private final Map<WicaChannelName, Integer> map = new HashMap<>();

/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/

   public WicaChannelDataNumericScaleSupplier( WicaStream wicaStream )
   {
      this.wicaStream = Validate.notNull( wicaStream );
      this.wicaStreamProperties = wicaStream.getWicaStreamProperties();
      this.wicaChannels = wicaStream.getWicaChannels();
   }

/*- Class methods ------------------------------------------------------------*/
/*- Public methods -----------------------------------------------------------*/

   @Override
   public int supplyForChannelNamed( WicaChannelName wicaChannelName)
   {
      return map.get( wicaChannelName );
   }


/*- Private methods ----------------------------------------------------------*/

   private void addWicaStreamPropertyDefaultValues()
   {
      final int numericScale = wicaStreamProperties.getNumericPrecision();
      logger.info( "Stream default numericScale is: '{}'", numericScale );
      map.keySet().forEach( (c) -> map.put( c, numericScale ) );
   }


   private void addWicaChannelPropertyOverrides()
   {
      wicaChannels.stream()
            .filter( ch -> ch.getProperties().getNumericPrecision() == null )
            .forEach( ch -> {
               final int numericScaleOverride = ch.getProperties().getNumericPrecision();
               logger.info("Channel '{}' had numericScale override '{}'", ch, numericScaleOverride );
               map.put(ch.getName(), numericScaleOverride);
            } );
   }

/*- Nested Classes -----------------------------------------------------------*/

}
