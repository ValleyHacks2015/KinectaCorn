
// SkeletonsGestures.java
// Andrew Davison, December 2011, ad@fivedots.psu.ac.th

/* Basic gestures detector.

   Examine a user's skeleton to see if it is just starting or stopping
   any of the gestures in GestureName. If it is then the watcher is notified.

   The notification consists of calling GesturesWatcher.pose() with the userID,
   the GestureName value, and a boolean denoted if the gesture has just started
   or finished.

   Many more gestures could be added to this class. For example, look at the
   gestures recognised by the Flexible Action and Articulated Skeleton Toolkit 
   (FAAST) at http://projects.ict.usc.edu/mxr/faast/


   Waving gestures are processed by the GestureSequences object which
   looks for sub-sequences of basic gestures that form "higher-level" gestures
   (only right hand waving at present).
   Currently, only the right hand gesture methods send gesture info
   to the GestureSequences object.

   OpenNI SkeletonJoint names:
      HEAD, NECK
      LEFT_SHOULDER, LEFT_ELBOW, LEFT_HAND
      RIGHT_SHOULDER, RIGHT_ELBOW, RIGHT_HAND
      TORSO
      LEFT_HIP, LEFT_KNEE, LEFT_FOOT
      RIGHT_HIP, RIGHT_KNEE, RIGHT_FOOT
*/

import java.util.*;
import org.OpenNI.*;



enum GestureName {
   HORIZ_WAVE, VERT_WAVE,                          // waving
   HANDS_NEAR,                                     // two hands
   LEAN_LEFT, LEAN_RIGHT, LEAN_FWD, LEAN_BACK,     // leaning
   TURN_RIGHT, TURN_LEFT,                          // turning
   LH_LHIP, RH_RHIP,                               // touching
   RH_UP, RH_FWD, RH_OUT, RH_IN, RH_DOWN,          // right hand position
   LH_UP, LH_FWD, LH_OUT, LH_IN, LH_DOWN,          // left hand position
}


public class SkeletonsGestures
{
  // standard skeleton lengths 
  private static final float NECK_LEN = 50.0f;
  private static final float LOWER_ARM_LEN = 150.0f;
  private static final float ARM_LEN = 400.0f;


  private GesturesWatcher watcher;
      // object that is notified of an gesture start/stop by calling its pose() method

  private HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> userSkels;
     /* skeleton joints for each user; uses screen coordinate system
          i.e.     positive z-axis is into the scene;
                   positive x-axis is to the right;
                   positive y-axis is down
     */

  private GestureSequences gestSeqs;
            /* stores gesture sequences for each user, and looks for 
               more complex gestures (actually only right hand 
               waving at present)  */

  /* skeleton lengths between joint pairs, 
    used when judging the distance between other joints */
  private float neckLength = NECK_LEN;            // neck to shoulder length
  private float lowerArmLength = LOWER_ARM_LEN;   // hand to elbow length
  private float armLength = ARM_LEN;              // hand to shoulder length


  // booleans set when gestures are being performed

  private boolean areHandsNear = false;       // two hands

  private boolean isLeanLeft = false;         // leaning
  private boolean isLeanRight = false;
  private boolean isLeanFwd = false;
  private boolean isLeanBack = false;

  private boolean isTurnLeft = false;        // turning
  private boolean isTurnRight = false; 

  private boolean lHandTouchlHip = false;     // touching
  private boolean rHandTouchrHip = false;

  private boolean isRightHandUp = false;      // right hand
  private boolean isRightHandFwd = false;
  private boolean isRightHandOut = false;
  private boolean isRightHandIn = false;
  private boolean isRightHandDown = false;

  private boolean isLeftHandUp = false;       // left hand
  private boolean isLeftHandFwd = false;
  private boolean isLeftHandOut = false;
  private boolean isLeftHandIn = false;
  private boolean isLeftHandDown = false;


  public SkeletonsGestures(GesturesWatcher aw,
            HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> uSkels,
            GestureSequences gSeqs)
  {  
    watcher = aw;
    userSkels = uSkels;
    gestSeqs = gSeqs;
  }  // end of SkeletonsGestures()




  public void checkGests(int userID)
  /* decide which gestures have just started or just finished, and
     notify the watcher. */
  {
    HashMap<SkeletonJoint, SkeletonJointPosition> skel = userSkels.get(userID);
    if (skel == null)
      return;

    calcSkelLengths(skel);
      /* repeatedly calculate lengths since the size of a skeleton *on-screen* will
         change if the user moves closer or further away. This overhead would
         disappear if skeletons were stored using real-world coordinates instead
         of screen-based values. */

    // uncomment the gestures that you want to detect...

    twoHandsNear(userID, skel);

    leanLeft(userID, skel);
/*
    leanRight(userID, skel);
    leanFwd(userID, skel); 
    leanBack(userID, skel);
*/
    turnLeft(userID, skel);
    // turnRight(userID, skel);

    // leftHandTouchHip(userID, skel);
    // rightHandTouchHip(userID, skel);

    rightHandUp(userID, skel);
    rightHandFwd(userID, skel);
    rightHandOut(userID, skel);
    rightHandIn(userID, skel);
    rightHandDown(userID, skel);

    leftHandUp(userID, skel);
    leftHandFwd(userID, skel);
    leftHandOut(userID, skel);
    leftHandIn(userID, skel);
    leftHandDown(userID, skel);
  }  // end of checkGests()



  private void calcSkelLengths(HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  /* calculate lengths between certain joint pairs for this skeleton;
     these values are used later to judge the distances between other joints 
  */
  {
    Point3D neckPt = getJointPos(skel, SkeletonJoint.NECK);
    Point3D shoulderPt = getJointPos(skel, SkeletonJoint.RIGHT_SHOULDER);
    Point3D handPt = getJointPos(skel, SkeletonJoint.RIGHT_HAND);
    Point3D elbowPt = getJointPos(skel, SkeletonJoint.RIGHT_ELBOW);

    if ((neckPt != null) && (shoulderPt != null) && 
        (handPt != null) && (elbowPt != null)) {
      neckLength = distApart(neckPt, shoulderPt);    // neck to shoulder length
      // System.out.println("Neck Length: " + neckLength);

      armLength = distApart(handPt, shoulderPt);     // hand to shoulder length
      // System.out.println("Arm length: " + armLength);

      lowerArmLength = distApart(handPt, elbowPt);    // hand to elbow length
      // System.out.println("Lower arm length: " + lowerArmLength);
    }
  }  // end of calcSkelLengths()



  private float distApart(Point3D p1, Point3D p2)
  // the Euclidian distance between the two points
  {
    float dist = (float) Math.sqrt( 
             (p1.getX() - p2.getX())*(p1.getX() - p2.getX()) +
             (p1.getY() - p2.getY())*(p1.getY() - p2.getY()) +
             (p1.getZ() - p2.getZ())*(p1.getZ() - p2.getZ()) );
    return dist;
  }  // end of distApart()



  // -------------------------- two hands ----------------------------------



  private void twoHandsNear(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // are the user's hand close together on the x-axis?
  {
    Point3D leftHandPt = getJointPos(skel, SkeletonJoint.LEFT_HAND);
    Point3D rightHandPt = getJointPos(skel, SkeletonJoint.RIGHT_HAND);
    if ((leftHandPt == null) || (rightHandPt == null))
      return;

    float xDiff = rightHandPt.getX() - leftHandPt.getX();
    // System.out.println(xDiff);
    if (xDiff < neckLength) {    // near
      if (!areHandsNear) {
        watcher.pose(userID, GestureName.HANDS_NEAR, true);   // started
        areHandsNear = true;
      }
    }
    else {   // not near
      if (areHandsNear) {
        watcher.pose(userID, GestureName.HANDS_NEAR, false);  // stopped
        areHandsNear = false;
      }
    }
  }  // end of twoHandsNear()




  // -------------------------- leaning ----------------------------------


  private void leanLeft(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's head to the left of his left hip?
  {
    Point3D leftHipPt = getJointPos(skel, SkeletonJoint.LEFT_HIP);
    Point3D headPt = getJointPos(skel, SkeletonJoint.HEAD);
    if ((leftHipPt == null) || (headPt == null))
      return;

    if (headPt.getX() <= leftHipPt.getX()) {    // to the left
      if (!isLeanLeft) {
        watcher.pose(userID, GestureName.LEAN_LEFT, true);   // started
        isLeanLeft = true;
      }
    }
    else {   // not to the left
      if (isLeanLeft) {
        watcher.pose(userID, GestureName.LEAN_LEFT, false);  // stopped
        isLeanLeft = false;
      }
    }
  }  // end of leanLeft()



  private void leanRight(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's head to the right of his right hip?
  {
    Point3D rightHipPt = getJointPos(skel, SkeletonJoint.RIGHT_HIP);
    Point3D headPt = getJointPos(skel, SkeletonJoint.HEAD);
    if ((rightHipPt == null) || (headPt == null))
      return;

    if (rightHipPt.getX() <= headPt.getX()) {    // to the right
      if (!isLeanRight) {
        watcher.pose(userID, GestureName.LEAN_RIGHT, true);  // started
        isLeanRight = true;
      }
    }
    else {   // not to the right
      if (isLeanRight) {
        watcher.pose(userID, GestureName.LEAN_RIGHT, false);  // stopped
        isLeanRight = false;
      }
    }
  }  // end of leanRight()




  private void leanFwd(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's head forward of his torso?
  {
    Point3D torsoPt = getJointPos(skel, SkeletonJoint.TORSO);
    Point3D headPt = getJointPos(skel, SkeletonJoint.HEAD);
    if ((torsoPt == null) || (headPt == null))
      return;

    float zDiff = headPt.getZ() - torsoPt.getZ();
    //   System.out.println(zDiff);

    if (zDiff < -1*lowerArmLength) {    // head is forward
      if (!isLeanFwd) {
        watcher.pose(userID, GestureName.LEAN_FWD, true);  // started
        isLeanFwd = true;
      }
    }
    else {   // not forward
      if (isLeanFwd) {
        watcher.pose(userID, GestureName.LEAN_FWD, false);  // stopped
        isLeanFwd = false;
      }
    }
  }  // end of leanFwd()



  private void leanBack(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's head leaning back relative to his torso?
  {
    Point3D torsoPt = getJointPos(skel, SkeletonJoint.TORSO);
    Point3D headPt = getJointPos(skel, SkeletonJoint.HEAD);
    if ((torsoPt == null) || (headPt == null))
      return;

    float zDiff = headPt.getZ() - torsoPt.getZ();
    //   System.out.println(zDiff);

    if (zDiff > lowerArmLength) {    // head is behind
      if (!isLeanBack) {
        watcher.pose(userID, GestureName.LEAN_BACK, true);  // started
        isLeanBack = true;
      }
    }
    else {   // not behind
      if (isLeanBack) {
        watcher.pose(userID, GestureName.LEAN_BACK, false);  // stopped
        isLeanBack = false;
      }
    }
  }  // end of leanBack()




  // -------------------------- turning ----------------------------------


  private void turnLeft(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // has the user's right hip turned forward to be in front of his left hip?
  {
    Point3D rightHipPt = getJointPos(skel, SkeletonJoint.RIGHT_HIP);
    Point3D leftHipPt = getJointPos(skel, SkeletonJoint.LEFT_HIP);
    if ((rightHipPt == null) || (leftHipPt == null))
      return;

    float zDiff = leftHipPt.getZ() - rightHipPt.getZ();
    //  System.out.println(zDiff);

    if (zDiff > lowerArmLength) {    // right hip is forward
      if (!isTurnLeft) {
        watcher.pose(userID, GestureName.TURN_LEFT, true);  // started
        isTurnLeft = true;
      }
    }
    else {   // not forward
      if (isTurnLeft) {
        watcher.pose(userID, GestureName.TURN_LEFT, false);  // stopped
        isTurnLeft = false;
      }
    }
  }  // end of turnLeft()



  private void turnRight(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // has the user's left hip turned forward to be in front of his right hip?
  {
    Point3D rightHipPt = getJointPos(skel, SkeletonJoint.RIGHT_HIP);
    Point3D leftHipPt = getJointPos(skel, SkeletonJoint.LEFT_HIP);
    if ((rightHipPt == null) || (leftHipPt == null))
      return;

    float zDiff = rightHipPt.getZ() - leftHipPt.getZ();
    //  System.out.println(zDiff);

    if (zDiff > lowerArmLength) {    // left hip is forward
      if (!isTurnRight) {
        watcher.pose(userID, GestureName.TURN_RIGHT, true);  // started
        isTurnRight = true;
      }
    }
    else {   // not forward
      if (isTurnRight) {
        watcher.pose(userID, GestureName.TURN_RIGHT, false);  // stopped
        isTurnRight = false;
      }
    }
  }  // end of turnRight()



  // -------------------------- touching ----------------------------------


  private void leftHandTouchHip(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's left hand touching his left hip?
  {
    Point3D leftHandPt = getJointPos(skel, SkeletonJoint.LEFT_HAND);
    Point3D leftHipPt = getJointPos(skel, SkeletonJoint.LEFT_HIP);
    if ((leftHandPt == null) || (leftHipPt == null))
      return;

    float dist = distApart(leftHipPt, leftHandPt);
    // System.out.println("dist: " + dist);

    if (dist < neckLength) {   // is touching
      if (!lHandTouchlHip) {
        watcher.pose(userID, GestureName.LH_LHIP, true);  // started
        lHandTouchlHip = true;
      }
    }
    else {   // not touching
      if (lHandTouchlHip) {
        watcher.pose(userID, GestureName.LH_LHIP, false);  // stopped
        lHandTouchlHip = false;
      }
    }
  }  // end of leftHandTouchHip()




  private void rightHandTouchHip(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's right hand touching his right hip?
  {
    Point3D rightHandPt = getJointPos(skel, SkeletonJoint.RIGHT_HAND);
    Point3D rightHipPt = getJointPos(skel, SkeletonJoint.RIGHT_HIP);
    if ((rightHandPt == null) || (rightHipPt == null))
      return;

    float dist = distApart(rightHipPt, rightHandPt);
    // System.out.println("dist: " + dist);

    if (dist < neckLength) {   // is touching
      if (!rHandTouchrHip) {
        watcher.pose(userID, GestureName.RH_RHIP, true);  // started
        rHandTouchrHip = true;
      }
    }
    else {   // not touching
      if (rHandTouchrHip) {
        watcher.pose(userID, GestureName.RH_RHIP, false);  // stopped
        rHandTouchrHip = false;
      }
    }
  }  // end of rightHandTouchHip()




  // -------------------------- right hand ----------------------------------
  /* the right hand gesture checking methods notify the GestureSequences 
     object of an gesture start so that it can update the user's gesture sequence.

     All the other gesture checking methods could do this, but I've kept things
     simple by only considering the right hand.
  */



  private void rightHandUp(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's right hand at head level or above?
  {
    Point3D rightHandPt = getJointPos(skel, SkeletonJoint.RIGHT_HAND);
    Point3D headPt = getJointPos(skel, SkeletonJoint.HEAD);
    if ((rightHandPt == null) || (headPt == null))
      return;

    if (rightHandPt.getY() <= headPt.getY()) {    // above
      if (!isRightHandUp) {
        watcher.pose(userID, GestureName.RH_UP, true);  // started
        gestSeqs.addUserGest(userID, GestureName.RH_UP);  // add to gesture sequence
        isRightHandUp = true;
      }
    }
    else {   // not above
      if (isRightHandUp) {
        watcher.pose(userID, GestureName.RH_UP, false);  // stopped
        isRightHandUp = false;
      }
    }
  }  // end of rightHandUp()




  private void rightHandFwd(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's right hand forward of his right shoulder?
  {
    Point3D rightHandPt = getJointPos(skel, SkeletonJoint.RIGHT_HAND);
    Point3D shoulderPt = getJointPos(skel, SkeletonJoint.RIGHT_SHOULDER);
    if ((rightHandPt == null) || (shoulderPt == null))
      return;

    float zDiff = rightHandPt.getZ() - shoulderPt.getZ();
    // System.out.println("diff: " + zDiff);

    if (zDiff < -1*(armLength*0.95f)) {    // is forward
      // System.out.println("  armLength: " + armLength);
      if (!isRightHandFwd) {
        watcher.pose(userID, GestureName.RH_FWD, true);  // started
        gestSeqs.addUserGest(userID, GestureName.RH_FWD);  // add to gesture sequence
        isRightHandFwd = true;
      }
    }
    else {   // not forward
      if (isRightHandFwd) {
        watcher.pose(userID, GestureName.RH_FWD, false);  // stopped
        isRightHandFwd = false;
      }
    }
  }  // end of rightHandFwd()



  private void rightHandOut(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's right hand out to the right of the his right elbow?
  {
    Point3D rightHandPt = getJointPos(skel, SkeletonJoint.RIGHT_HAND);
    Point3D elbowPt = getJointPos(skel, SkeletonJoint.RIGHT_ELBOW);
    if ((rightHandPt == null) || (elbowPt == null))
      return;

    float xDiff = rightHandPt.getX() - elbowPt.getX();

    if (xDiff > (lowerArmLength*0.6f)) {    // out to the right
      if (!isRightHandOut) {
        watcher.pose(userID, GestureName.RH_OUT, true);  // started
        gestSeqs.addUserGest(userID, GestureName.RH_OUT);  // add to gesture sequence
        isRightHandOut = true;
      }
    }
    else {   // not out to the right
      if (isRightHandOut) {
        watcher.pose(userID, GestureName.RH_OUT, false);  // stopped
        isRightHandOut = false;
      }
    }
  }  // end of rightHandOut()



  private void rightHandIn(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's right hand inside (left) of his right elbow?
  {
    Point3D rightHandPt = getJointPos(skel, SkeletonJoint.RIGHT_HAND);
    Point3D elbowPt = getJointPos(skel, SkeletonJoint.RIGHT_ELBOW);
    if ((rightHandPt == null) || (elbowPt == null))
      return;

    float xDiff = rightHandPt.getX() - elbowPt.getX();

    if (xDiff < -1*(lowerArmLength*0.6f)) {   // inside
      if (!isRightHandIn) {
        watcher.pose(userID, GestureName.RH_IN, true);  // started
        gestSeqs.addUserGest(userID, GestureName.RH_IN);  // add to gesture sequence
        isRightHandIn = true;
      }
    }
    else {   // not inside
      if (isRightHandIn) {
        watcher.pose(userID, GestureName.RH_IN, false);  // stopped
        isRightHandIn = false;
      }
    }
  }  // end of rightHandIn()



  private void rightHandDown(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's right hand at hip level or below?
  {
    Point3D rightHandPt = getJointPos(skel, SkeletonJoint.RIGHT_HAND);
    Point3D hipPt = getJointPos(skel, SkeletonJoint.RIGHT_HIP);
    if ((rightHandPt == null) || (hipPt == null))
      return;

    if (rightHandPt.getY() >= hipPt.getY()) {    // below
      if (!isRightHandDown) {
        watcher.pose(userID, GestureName.RH_DOWN, true);  // started
        gestSeqs.addUserGest(userID, GestureName.RH_DOWN);  // add to gesture sequence
        isRightHandDown = true;
      }
    }
    else {   // not below
      if (isRightHandDown) {
        watcher.pose(userID, GestureName.RH_DOWN, false);  // stopped
        isRightHandDown = false;
      }
    }
  }  // end of rightHandDown()




  // -------------------------- left hand ----------------------------------


  private void leftHandUp(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's left hand at head level or above?
  {
    Point3D leftHandPt = getJointPos(skel, SkeletonJoint.LEFT_HAND);
    Point3D headPt = getJointPos(skel, SkeletonJoint.NECK);
    if ((leftHandPt == null) || (headPt == null))
      return;

    if (leftHandPt.getY() <= headPt.getY()) {    // above
      if (!isLeftHandUp) {
        watcher.pose(userID, GestureName.LH_UP, true);  // started
        isLeftHandUp = true;
      }
    }
    else {   // not above
      if (isLeftHandUp) {
        watcher.pose(userID, GestureName.LH_UP, false);  // stopped
        isLeftHandUp = false;
      }
    }
  }  // end of leftHandUp()



  private void leftHandFwd(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's right hand forward of his right shoulder?
  {
    Point3D leftHandPt = getJointPos(skel, SkeletonJoint.LEFT_HAND);
    Point3D shoulderPt = getJointPos(skel, SkeletonJoint.LEFT_SHOULDER);
    if ((leftHandPt == null) || (shoulderPt == null))
      return;

    float zDiff = leftHandPt.getZ() - shoulderPt.getZ();
    // System.out.println("diff: " + zDiff);

    if (zDiff < -1*(armLength*0.95f)) {    // is forward
      // System.out.println("  armLength: " + armLength);
      if (!isLeftHandFwd) {
        watcher.pose(userID, GestureName.LH_FWD, true);  // started
        gestSeqs.addUserGest(userID, GestureName.LH_FWD);  // add to gesture sequence
        isLeftHandFwd = true;
      }
    }
    else {   // not forward
      if (isLeftHandFwd) {
        watcher.pose(userID, GestureName.LH_FWD, false);  // stopped
        isLeftHandFwd = false;
      }
    }
  }  // end of rightHandFwd()



  private void leftHandOut(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's right hand out to the right of the his right elbow?
  {
    Point3D leftHandPt = getJointPos(skel, SkeletonJoint.LEFT_HAND);
    Point3D elbowPt = getJointPos(skel, SkeletonJoint.LEFT_ELBOW);
    if ((leftHandPt == null) || (elbowPt == null))
      return;

    float xDiff = leftHandPt.getX() - elbowPt.getX();

    if (xDiff > (lowerArmLength*0.6f)) {    // out to the right
      if (!isLeftHandOut) {
        watcher.pose(userID, GestureName.LH_OUT, true);  // started
        gestSeqs.addUserGest(userID, GestureName.LH_OUT);  // add to gesture sequence
        isLeftHandOut = true;
      }
    }
    else {   // not out to the right
      if (isLeftHandOut) {
        watcher.pose(userID, GestureName.LH_OUT, false);  // stopped
        isLeftHandOut = false;
      }
    }
  }  // end of rightHandOut()



  private void leftHandIn(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's right hand inside (left) of his right elbow?
  {
    Point3D leftHandPt = getJointPos(skel, SkeletonJoint.LEFT_HAND);
    Point3D elbowPt = getJointPos(skel, SkeletonJoint.LEFT_ELBOW);
    if ((leftHandPt == null) || (elbowPt == null))
      return;

    float xDiff = leftHandPt.getX() - elbowPt.getX();

    if (xDiff < -1*(lowerArmLength*0.6f)) {   // inside
      if (!isLeftHandIn) {
        watcher.pose(userID, GestureName.LH_IN, true);  // started
        gestSeqs.addUserGest(userID, GestureName.LH_IN);  // add to gesture sequence
        isLeftHandIn = true;
      }
    }
    else {   // not inside
      if (isLeftHandIn) {
        watcher.pose(userID, GestureName.LH_IN, false);  // stopped
        isLeftHandIn = false;
      }
    }
  }  // end of rightHandIn()



  private void leftHandDown(int userID, HashMap<SkeletonJoint, SkeletonJointPosition> skel)
  // is the user's right hand at hip level or below?
  {
    Point3D leftHandPt = getJointPos(skel, SkeletonJoint.LEFT_HAND);
    Point3D hipPt = getJointPos(skel, SkeletonJoint.LEFT_HIP);
    if ((leftHandPt == null) || (hipPt == null))
      return;

    if (leftHandPt.getY() >= hipPt.getY()) {    // below
      if (!isLeftHandDown) {
        watcher.pose(userID, GestureName.LH_DOWN, true);  // started
        gestSeqs.addUserGest(userID, GestureName.LH_DOWN);  // add to gesture sequence
        isLeftHandDown = true;
      }
    }
    else {   // not below
      if (isLeftHandDown) {
        watcher.pose(userID, GestureName.LH_DOWN, false);  // stopped
        isLeftHandDown = false;
      }
    }
  }  // end of rightHandDown()



  // ----------------------------- support -------------------------


  private Point3D getJointPos(HashMap<SkeletonJoint, SkeletonJointPosition> skel, 
                                                SkeletonJoint j)
  // get the (x, y, z) coordinate for the joint (or return null)
  {
    SkeletonJointPosition pos = skel.get(j);
    if (pos == null)
      return null;

    if (pos.getConfidence() == 0)
      return null;

    return pos.getPosition();
  }  // end of getJointPos()



}  // end of SkeletonsGestures class


