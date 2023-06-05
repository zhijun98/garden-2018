/*
 * Copyright 2018 ZComApproach Inc.
 *
 * Licensed under multiple open source licenses involved in the project (the "Licenses");
 * you may not use this file except in compliance with the Licenses.
 * You may obtain copies of the Licenses at
 *
 *      http://www.zcomapproach.com/licenses
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zcomapproach.garden.peony.utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author zhijun98
 */
public class PeonyFileFilter extends FileFilter {
    private final String ext;

    public PeonyFileFilter(String ext) {
        ext = ext.toLowerCase().replaceAll("\\*", "").trim();
        ext = ext.replaceAll("\\.", "").trim();
        this.ext = "." + ext;

    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()){
            return true;
        }
        return f.isFile() && f.getName().toLowerCase().endsWith(ext);
    }

    @Override
    public String getDescription() {
        return ext;
    }
}
