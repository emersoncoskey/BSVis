package net.emersoncoskey.bsvis.components.mapview

import cats.effect.SyncIO
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import net.emersoncoskey.bsvis.data.beatsaber._
import net.emersoncoskey.bsvis.data.constants.BeatSaberSvgAssets
import net.emersoncoskey.bsvis.hooks.UseAnimationFrame
import org.scalajs.dom.html

object BloqView {
	final case class Props(bloq: Option[Bloq], timeSince: () => Double)

	private def bloqOpacity(timeSince: Double): Double = Math.max(0, 32 * Math.pow(-timeSince, 3) + 1)

	private def bloqRotStyle(dir: NoteDirection): String = {
		val degrees = dir match {
			case Dot => 0

			case U => 180
			case D => 0
			case L => 90
			case R => 270

			case UL => 135
			case UR => 225
			case DL => 45
			case DR => 315
		}

		s"rotate(${degrees}deg)"
	}

	def C: ScalaFnComponent[Props, CtorType.Props] = Component

	val Component: ScalaFnComponent[Props, CtorType.Props] =
		ScalaFnComponent.withHooks[Props]

		                .useRefToVdom[html.Image]

		                .customBy((props, ref) => UseAnimationFrame.H(_ =>
			                ref.foreach(_.style.opacity = bloqOpacity(props.timeSince()).toString)
		                ))

		                .render((props: Props, ref: Ref.ToVdom[html.Image]) => props match {
			                case Props(bloq, _) =>
				                <.div(
					                ^.width := "10em",
					                ^.height := "10em",
					                ^.backgroundColor := "gray",  // (gray?) background behind note -> maybe put second in list, idk how html work
					                ^.borderRadius := "15%",
					                ^.margin := "0.25em",

					                bloq.map {
						                case Note(color, dir) =>
							                <.img.withRef(ref)(
								                ^.opacity := "1",
								                ^.width := "10em",
								                ^.height := "10em",
								                ^.transform := bloqRotStyle(dir),
								                ^.src := BeatSaberSvgAssets.NoteAssetPath(color, dir),
							                )
						                case Bomb =>
							                <.div.withRef(ref)( //TODO: Replace with svg for bomb
								                ^.opacity := "1",
								                ^.width := "10em",
								                ^.height := "10em",
								                ^.backgroundColor := "red",
								                "bomb",
							                )
					                }
				                )
		                })

}