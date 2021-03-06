/* Copyright 2012 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */

package com.predic8.schema.diff

import java.util.ResourceBundle;

import com.predic8.soamodel.*
import com.predic8.schema.*

class AbstractModelDiffGenerator extends UnitDiffGenerator {
//  protected ResourceBundle bundle = ResourceBundle.getBundle("LabelsBundle", new Locale("en", "US"))
  def generator
  protected def labelParticle, labelRemoved, labelAdded
	
  List<Difference> compareUnit(){
    if (!b.metaClass.hasProperty(b, 'elements')) {
      return [new Difference(description: "${a.toString()} has changed to ${b.toString()}",breaks: ctx.exchange ? true: null)]
    }

    def diffs = new ElementsDiffGenerator(a: a.elements, b: b.elements, generator: generator, ctx: ctx.clone()).compare()
//    def aPs = (a.particles-a.elements)
    def aPs = a.particles.findAll { ap -> !a.elements.find { ap == it } }
//    def bPs = (b.particles-b.elements)
    def bPs = b.particles.findAll { bp -> !b.elements.find { bp == it } }
    aPs.each{ aP ->
      bPs.each{ bP ->
        if(bP.class != aP.class) return
        if(!(aP.compare(generator, bP))){
          aPs -= aP
          bPs -= bP
        }
      }
    }
    diffs.addAll(writeRemovedParticles(aPs))
    diffs.addAll(wriTeAddedParticles(bPs))
    diffs
  }
  
  public AbstractModelDiffGenerator(){
	  updateLabels()
  }
  
  def writeRemovedParticles(aPs){
    def diffs = []
    aPs.each{
      diffs << new Difference(description:"${labelParticle} ${it.elementName} ${labelRemoved}." , type: 'choice')
    }
    diffs
  }

  def wriTeAddedParticles(bPs){
    def diffs = []
    bPs.each{
      diffs << new Difference(description:"${labelParticle} ${it.elementName} ${labelAdded}." , type: 'choice')
    }
    diffs
  }
  
  protected def updateLabels(){
	  
  	labelParticle = bundle.getString("com.predic8.schema.diff.labelParticle")
  	labelRemoved = bundle.getString("com.predic8.schema.diff.labelRemoved")
  	labelAdded = bundle.getString("com.predic8.schema.diff.labelAdded")
  }
}

