/*- Package Declaration ------------------------------------------------------*/

package ch.psi.wica.model.app;

/*- Imported packages --------------------------------------------------------*/

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/*- Interface Declaration ----------------------------------------------------*/
/*- Class Declaration --------------------------------------------------------*/

@Service
@ThreadSafe
public class StatisticsCollectionService
{

/*- Public attributes --------------------------------------------------------*/
/*- Private attributes -------------------------------------------------------*/

   private final List<StatisticsCollectable> collectables = new ArrayList<>();

/*- Main ---------------------------------------------------------------------*/
/*- Constructor --------------------------------------------------------------*/
/*- Class methods ------------------------------------------------------------*/
/*- Public methods -----------------------------------------------------------*/

   public void addCollectable( StatisticsCollectable statisticsCollectable )
   {
      collectables.add( statisticsCollectable );
   }

   public List<StatisticsCollectable.StatisticsEntry> getEntries()
   {
      final List<StatisticsCollectable.StatisticsEntry> entries = collectables.stream()
            .map(StatisticsCollectable::getEntries)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList() );

      return entries;
   }

   public void resetStatistics()
   {
      collectables.forEach(StatisticsCollectable::clearEntries);
   }

   /*- Private methods ----------------------------------------------------------*/
/*- Nested Classes -----------------------------------------------------------*/

}