package org.robolectric.shadows;

import android.view.animation.LinearInterpolator;
import android.widget.OverScroller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.TestRunners;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(TestRunners.WithDefaults.class)
public class OverScrollerTest {
  private OverScroller overScroller;

  @Before
  public void setUp() {
    overScroller = new OverScroller(Robolectric.application, new LinearInterpolator());
  }

  @Test
  public void shouldScrollOverTime() {
    overScroller.startScroll(0, 0, 100, 200, 1000);

    assertThat(overScroller.getStartX()).isEqualTo(0);
    assertThat(overScroller.getStartY()).isEqualTo(0);
    assertThat(overScroller.getFinalX()).isEqualTo(100);
    assertThat(overScroller.getFinalY()).isEqualTo(200);
    assertThat(overScroller.isScrollingInDirection(1, 1)).isTrue();
    assertThat(overScroller.isScrollingInDirection(-1, -1)).isFalse();

    assertThat(overScroller.getCurrX()).isEqualTo(0);
    assertThat(overScroller.getCurrY()).isEqualTo(0);
    assertThat(overScroller.timePassed()).isEqualTo(0);
    assertThat(overScroller.isFinished()).isFalse();

    Robolectric.idleMainLooper(100);
    assertThat(overScroller.getCurrX()).isEqualTo(10);
    assertThat(overScroller.getCurrY()).isEqualTo(20);
    assertThat(overScroller.timePassed()).isEqualTo(100);
    assertThat(overScroller.isFinished()).isFalse();

    Robolectric.idleMainLooper(401);
    assertThat(overScroller.getCurrX()).isEqualTo(50);
    assertThat(overScroller.getCurrY()).isEqualTo(100);
    assertThat(overScroller.timePassed()).isEqualTo(501);
    assertThat(overScroller.isFinished()).isFalse();

    Robolectric.idleMainLooper(1000);
    assertThat(overScroller.getCurrX()).isEqualTo(100);
    assertThat(overScroller.getCurrY()).isEqualTo(200);
    assertThat(overScroller.timePassed()).isEqualTo(1501);
    assertThat(overScroller.isFinished()).isEqualTo(true);
    assertThat(overScroller.isScrollingInDirection(1, 1)).isFalse();
    assertThat(overScroller.isScrollingInDirection(-1, -1)).isFalse();
  }

  @Test
  public void computeScrollOffsetShouldCalculateWhetherScrollIsFinished() {
    assertThat(overScroller.computeScrollOffset()).isFalse();

    overScroller.startScroll(0, 0, 100, 200, 1000);
    assertThat(overScroller.computeScrollOffset()).isTrue();

    Robolectric.idleMainLooper(500);
    assertThat(overScroller.computeScrollOffset()).isTrue();

    Robolectric.idleMainLooper(500);
    assertThat(overScroller.computeScrollOffset()).isTrue();
    assertThat(overScroller.computeScrollOffset()).isFalse();
  }

  @Test
  public void abortAnimationShouldMoveToFinalPositionImmediately() {
    overScroller.startScroll(0, 0, 100, 200, 1000);
    Robolectric.idleMainLooper(500);
    overScroller.abortAnimation();

    assertThat(overScroller.getCurrX()).isEqualTo(100);
    assertThat(overScroller.getCurrY()).isEqualTo(200);
    assertThat(overScroller.timePassed()).isEqualTo(500);
    assertThat(overScroller.isFinished()).isTrue();
  }

  @Test
  public void forceFinishedShouldFinishWithoutMovingFurther() {
    overScroller.startScroll(0, 0, 100, 200, 1000);
    Robolectric.idleMainLooper(500);
    overScroller.forceFinished(true);

    assertThat(overScroller.getCurrX()).isEqualTo(50);
    assertThat(overScroller.getCurrY()).isEqualTo(100);
    assertThat(overScroller.timePassed()).isEqualTo(500);
    assertThat(overScroller.isFinished()).isTrue();
  }
}
