
// GesturesWatcher.java
// Based off Andrew Davison's open source
/* used to ensure that a watcher class can be contacted when
   a skeleton gesture starts (or stops)
*/

public interface GesturesWatcher 
{
  void pose(int userID, GestureName gest, boolean isActivated);
}
